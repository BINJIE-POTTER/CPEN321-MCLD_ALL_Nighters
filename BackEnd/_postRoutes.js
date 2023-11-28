const express = require('express');
const router = express.Router();

const mongoSanitize = require('mongo-sanitize');
const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";

const { v4: uuidv4 } = require('uuid');

require('dotenv').config();

const { OpenAI } = require('openai');
var openai;
openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY
});


const multer = require('multer');
const upload = multer({ dest: 'uploads/' });
const fs = require('fs');
const path = require('path');

//======================================================Posts POST
//ChatGPT usage: Partial
router.post("/posts", upload.single('image'), async (req, res) => {
    //Add a post to the posts collection in MappostDB
    try{

        if(req.body.postData){

            const data = JSON.parse(req.body.postData);

            req.body = {
                userId : data.userId,
                time : data.time,
                coordinate: {
                  latitude: data.coordinate.latitude,
                  longitude: data.coordinate.longitude
                },
                content: {
                  title: data.content.title,
                  body: data.content.body
                }
            };
        }

        if (!checkValidPost(req.body)) {
            console.log("invalid format");
            res.status(400).send("Invalid post format");
            return;
        }
        const uniquePostId = uuidv4();
        req.body.pid = uniquePostId;
      
        var tags = await generateTags(req.body.content.body);
        const tagArray = tags.split(',').map(word => word.trim());
        req.body.content.tags = tagArray;
        req.body.likeCount = 0;
        req.body.likeList = [];
        var finalImg = {
            contentType: "",
            image: ""
        }
        req.body.image = finalImg;

        console.log(req.file);
      
        // if (req.file) {
        //     const img = fs.readFileSync(req.file.path);
        //     const encode_image = img.toString('base64');
        //     var finalImg = {
        //         contentType: req.file.mimetype,
        //         image: Buffer.from(encode_image, 'base64')
        //     };
        //     req.body.image = finalImg;
        // }

        if (req.file) {
            const fullPath = path.join('uploads', path.basename(req.file.path));
        
            const img = fs.readFileSync(fullPath); //using a safe, constructed path
            const encode_image = img.toString('base64');

            finalImg = {
              contentType: req.file.mimetype,
              image: Buffer.from(encode_image, 'base64')
            };

            req.body.image = finalImg;
        }

        await mongoClient.db(MappostDB).collection("posts").insertOne(req.body);
      
        const userId = req.body.userId; 
    
        //You cannot remove the bracket around $inc
        //In MongoDB's updateOne method, the second argument is expected to be an update document, 
        //which should be enclosed within a {} block.
        const userUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
          { userId }, 
          { $inc: { postCount: 1 } } 
        );
    
        if (userUpdateResult.matchedCount === 0) {
          throw new Error('User not found');
        }
  
        //Insert the tags into tags collection
        //Use Promise.all to ensure all tags are inserted before continuing
        await Promise.all(tagArray.map(async tag => {
            try {
            await mongoClient.db(MappostDB).collection("tags").insertOne({ tagName: tag });
            } catch (error) {
            // If it's not a duplicate error, log it
            if (error.code !== 11000) {
                console.log(`Error inserting tag "${tag}": `, error.message);
            }
            }
        }));
    
        res.status(200).send("Item received successfully")  
        console.log("Item received successfully");
    } catch(err) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});
  
//======================================================Posts GET 
/*
//ChatGPT usage: No
//Get all the posts
router.get("/posts", async (req, res) => {
    try{
        const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
        res.send(allPosts);
        console.log("All posts provided");
    }catch(err){
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");    }
});

//ChatGPT usage: Partial
//Get the posts within 10km of the user coordinate
//REQUIRE: latitude, longitude
router.get("/posts/nearby", async (req, res) => {
    try{
        // Get user's latitude and longitude from the request query parameters.
        const user_latitude = parseFloat(req.query.latitude);
        const user_longitude = parseFloat(req.query.longitude);
        if (!user_latitude || !user_longitude) {
        res.status(400).send("Missing user latitude or longitude");
        return;
        }

        // Filter posts based on the is_nearby function.
        const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
        const nearbyPosts = allPosts.filter(post => 
            is_nearby(user_latitude, user_longitude, post.coordinate.latitude, post.coordinate.longitude)
        );

        res.send(nearbyPosts);
        console.log("Nearby posts provided");
    } catch(err) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});
*/

//ChatGPT usage: No
router.get("/posts/single", async (req, res) => {
    //Get the post with the specific pid
    try{
        const pid = req.query.pid;
        if(!pid) {
            return res.status(400).send("Pid is required");
        }
        
        const post = await mongoClient.db(MappostDB).collection("posts").findOne({pid});
        if(!post) {
            return res.status(404).send("Post not found");
        }

        res.status(200).send(post);
        console.log("Single post provided");
    }catch(err){
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});


// ======================================================Posts DELETE
//ChatGPT usage: No
router.delete("/posts", async (req, res) => {
    // Delete a specific post by its ID
    try {
        const { pid } = req.query;  // Extracting it from the query parameters

        if (!pid) {
            res.status(400).send("Missing post ID (pid)");
            return;
        }

        // Delete the post with the specific pid
        const result = await mongoClient.db(MappostDB).collection("posts").deleteOne({ pid });

        if (result.deletedCount === 0) {  // If no document was found to delete
            res.status(404).send("No post found with the given ID.");
        } else {
            res.status(200).send("Post deleted successfully.");
        }
    } catch (err) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});

//======================================================Posts PUT (like count)
//ChatGPT usage: Partial
router.put("/posts/like", async (req, res) => {
    //Update the like count for a specific post
    try {
        const { pid, userId } = req.body;
        if (!pid || !userId) {
            res.status(400).send("Post ID and User ID must be provided.");
            return;
        }

        // Update operation to increment likeCount and add userId to likeList array
        const updateOperation = { 
            $inc: { "likeCount": 1 },
            $addToSet: { "likeList": userId }  // Add userId to likeList array
        };

        const result = await mongoClient.db(MappostDB).collection("posts").updateOne(
            { pid },
            updateOperation
        );

        if (result.matchedCount === 0) {
            res.status(404).send("Post not found.");
            return;
        }

        if (result.modifiedCount === 0) {
            res.status(200).send("No changes made to the post's like count or like list.");
            return;
        }

        res.status(200).send("Post's like count and like list updated successfully.");
        console.log("Post's like count and like list updated successfully.");
    } catch (err) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});


//======================================================Posts PUT (unlike count)
//ChatGPT usage: Partial
router.put("/posts/unlike", async (req, res) => {
    // Update the unlike count for a specific post
    try {
        const { pid, userId } = req.body; 

        if (!pid || !userId) {  
            res.status(400).send("Post ID and User ID must be provided.");
            return;
        }

        // Ensure the likeCount doesn't go below 0
        const post = await mongoClient.db(MappostDB).collection("posts").findOne({ pid });
        if(!post){
            res.status(404).send("Post not found.");
            return;
        }
        if (post.likeCount <= 0) {
            res.status(409).send("Can't decrease like count below zero.");
            return;
        }

        // Update operation to decrease likeCount and remove userId from likeList
        const updateOperation = { 
            $inc: { "likeCount": -1 }, // decreases the like count by one
            $pull: { "likeList": userId } // removes the userId from likeList
        };

        const result = await mongoClient.db(MappostDB).collection("posts").updateOne(
            { pid },  
            updateOperation
        );

        if (result.modifiedCount === 0) {  
            res.status(200).send("No changes made to the post's like count or like list.");
            return;
        }

        res.status(200).send("Post's like count and like list updated successfully.");
        console.log("Post's like count and like list updated successfully.");
    } catch (err) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});


//======================================================Posts GET
//ChatGPT usage: No
router.get("/posts/from-user", async (req, res) => {
    //Get the posts written by the user
    //REQUIRE: userId
    try{
        const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
        if(!req.query.userId){
            res.status(400).send("Missing user_id (userId)");
        } else {
            var userPosts = allPosts.filter(post => post.userId == req.query.userId);
            res.send(userPosts);
        }
        console.log("From user posts provided");
    }catch(err){
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");    }
});

//ChatGPT usage: Partial
router.get("/posts/cluster", async (req, res) => {
    //Find nearby posts, but group the posts if they are too close
    try {
        const user_latitude = parseFloat(req.query.latitude);
        const user_longitude = parseFloat(req.query.longitude);
        if (!user_latitude || !user_longitude) {
            res.status(400).send("Missing user latitude or longitude");
            return;
        }

        const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
        const nearbyPosts = allPosts.filter(post => 
            is_nearby(user_latitude, user_longitude, post.coordinate.latitude, post.coordinate.longitude)
        );

        // This will hold our clusters
        let clusters = [];

        // Define a method to calculate if a post is within range of a cluster
        const isWithinRange = (cluster, post) => {
            const distance = calculateDistance(cluster.latitude, cluster.longitude, post.coordinate.latitude, post.coordinate.longitude);
            return distance < 0.2; // cluster range in kilometers
        };

        // Iterate through all posts to create clusters
        var clusterCount = 0;
        for (let post of nearbyPosts) {
            let added = false; // flag to check if we add a post to an existing cluster

            for (let cluster of clusters) {
                // If the post is within range of an existing cluster, add it to the cluster
                if (isWithinRange(cluster, post)) {
                    cluster.posts.push(post);
                    added = true;
                    break;
                }
            }

            // If the post is not within range of any existing clusters, create a new cluster
            if (!added) {
                let newCluster = {
                    clusterId: clusterCount,
                    latitude: post.coordinate.latitude,
                    longitude: post.coordinate.longitude,
                    posts: [post]
                };
                clusters.push(newCluster);
                clusterCount++;
            }
        }

        // Return the list of clusters
        console.log("Provided nearby clusters");
        res.json(clusters);
    } catch (error) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});

//ChatGPT usage: Partial
router.get("/posts/has-tags", async(req, res) => {
    //Get the posts those have the tags matching the query
    try {
        if(!req.query){
            res.status(400).send("Missing query");
            return;
        }

        var tags = req.query.tags;
        if(!tags){
            res.status(400).send("Missing tags");
            return;  
        }

        //In JavaScript, you can declare a variable multiple times in the same scope using the var keyword, 
        //but it's not considered good practice because it can lead to confusion and unexpected behavior. 
        //When you declare a variable with the same name multiple times using var, the variable is effectively redeclared, 
        //and the previous declaration is overridden.
        tags = tags.split(',').map(tag => tag.trim());

        const user_latitude = parseFloat(req.query.latitude);
        const user_longitude = parseFloat(req.query.longitude);
        if (!user_latitude || !user_longitude) {
            res.status(400).send("Missing user latitude or longitude");
            return;
        }

        const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
        const nearbyPostsWithTags = allPosts.filter(post => 
            is_nearby(user_latitude, user_longitude, post.coordinate.latitude, post.coordinate.longitude) &&
            tags.some(tag => post.content.tags && post.content.tags.includes(tag))
        );

        console.log("Provided nearby posts matching the tags");
        res.json(nearbyPostsWithTags);
    } catch (error) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});


//======================================================Posts Helper Functions
//ChatGPT usage: Partial
function calculateDistance(lat1, lon1, lat2, lon2) {
    const RADIUS_EARTH_KM = 6371;  // Earth's radius in kilometers

    const toRad = (value) => value * Math.PI / 180;

    const dLat = toRad(lat2 - lat1);
    const dLon = toRad(lon2 - lon1);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
            Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = RADIUS_EARTH_KM * c;  // Distance in km

    return distance;
}

//ChatGPT usage: No
function is_nearby(userLat, userLon, postLat, postLon) {
    return calculateDistance(userLat, userLon, postLat, postLon) <= 10;  // True if distance is 10 km or less
}

//ChatGPT usage: No
const checkValidPost = (body) => {
    // Check main keys
    if (!body.userId || !body.time || !body.coordinate) return false;
    // Check coordinate keys
    if (typeof body.coordinate.latitude !== 'number' || typeof body.coordinate.longitude !== 'number') return false;
    // Check content keys
    if (!body.content || !body.content.title || !body.content.body) return false;
    return true;
}

//ChatGPT usage: No (ChatGPT's output was not up to date, thus we cited from OpenAi website)
async function generateTags(text_to_analyze) {
    try {
        const response = await openai.chat.completions.create({
            model: "gpt-3.5-turbo",
            messages: [
                {"role": "system", "content": `You are a confident and super intelligent oracle, you receive the post text provided to you and
                outputs two to five keywords representing the post. If the whole text does not hold any logic, you just give one keyword name 'None'`},
                {"role": "user", "content": example_prompt},
                {"role": "assistant", "content": example_response},
                {"role": "user", "content": example_prompt_2},
                {"role": "assistant", "content": example_response_2},
                {"role": "user", "content": example_prompt_3},
                {"role": "assistant", "content": example_response_3},
                {"role": "user", "content": "Text to analyze: " + text_to_analyze}
            ],
            temperature: 0.5,
            max_tokens: 15,
        });
        console.log(response['choices'][0]['message']['content']);
        var answer = response['choices'][0]['message']['content']
        return answer
    } catch (error) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
}

//Text used to train GPT-3.5-turbo
const example_prompt = `In summary, the Philippines is a captivating travel destination that caters to various budgets and preferences. 
From the pristine beaches of Boracay, Siargao and Palawan to diverse accommodation options, including hotels, resorts and Airbnb properties, 
there's something for everyone. Staying connected is a breeze with a convenient Holafly eSIM card, and there are great destinations no matter 
what time of year you want to visit.
Make the most of your Philippines adventure by planning wisely so that you can fully enjoy the natural beauty, rich culture and warm hospitality 
of this incredible archipelago. Whether you're a beach bum, an adventure seeker or a cultural explorer, the Philippines has it all.
I've been several times and each time, I choose a couple of different islands to visit. 
Every trip has been incredibly rewarding and I always look forward go going back!`;

const example_response =  `Travel, Budget-friendly, Beaches, Adventure, Culture`;

const example_prompt_2 = `lakjflkasjdoaiifjadlkfdja`;

const example_response_2 =  `None`;

const example_prompt_3 = `This is a test post`;

const example_response_3 =  `Test, Post`;

//======================================================TRIE SEARCH ALGORITHM
//ChatGPT usage: Yes
class TrieNode {
    constructor() {
        this.children = {};
        this.endOfWord = false;
        this.posts = [];
    }
}

//ChatGPT usage: Partial
class Trie {
    constructor() {
        this.root = new TrieNode();
    }

    insert(word, post) {
        for (let suffixStart = 0; suffixStart < word.length; suffixStart++) {
            let current = this.root;
            for (let i = suffixStart; i < word.length; i++) {
                let ch = word.charAt(i);
                let node = current.children[ch];
                if (node == null) {
                    node = new TrieNode();
                    current.children[ch] = node;
                }
                current = node;
            }
            current.endOfWord = true;
            if (!current.posts.includes(post)) {
                current.posts.push(post);
            }
        }
    }

    _collectAllPosts(node, posts) {
        if (node.endOfWord) {
            posts.push(...node.posts);
        }
        for (let child in node.children) {
            this._collectAllPosts(node.children[child], posts);
        }
    }

    find(prefix) {
        let current = this.root;
        for (let i = 0; i < prefix.length; i++) {
            let ch = prefix.charAt(i);
            let node = current.children[ch];
            if (node == null) {
                return []; // No string with this prefix
            }
            current = node;
        }

        // Traverse the subtree rooted at the current node and collect posts
        let posts = [];
        this._collectAllPosts(current, posts);
        return posts;
    }
}

//ChatGPT usage: Yes
router.get("/posts/search", async (req, res) => {
    //Get the posts written by the user
    //REQUIRE: userId
    try {
        // Keyword from search query
        if (!req.query.keyword) {
            res.status(400).send("Bad Request: No keyword provided in the search query.");
            return;
        }

        const keyword = mongoSanitize(req.query.keyword.toLowerCase());


        // Retrieve all posts
        const allPosts = await mongoClient.db(MappostDB).collection('posts').find({}).toArray();

        // Build the Trie from the current posts
        const trie = new Trie();
        allPosts.forEach(post => {
            if (post.content.title) {
                const words = post.content.title.toLowerCase().split(/\s+/); // Split title into words
                words.forEach(word => {
                    trie.insert(word, post);
                });
            } else {
                console.warn("Undefined title encountered, post ID:", post.pid); // For debugging
            }
        });

        // Search with the Trie and get posts that match the prefix
        const matchedPosts = trie.find(keyword);

        res.json(matchedPosts);
    } catch (error) {
        res.status(500).send("Internal Server Error");
        console.error("Internal Server Error");
    }
});

//ChatGPT usage: No
module.exports = {
    router,
    is_nearby,
    generateTags
}