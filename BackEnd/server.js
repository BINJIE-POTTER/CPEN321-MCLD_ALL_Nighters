//Backend-server used to connect to mongodb
var express = require("express")

var app = express()
app.use(express.json())

// const getPublicIp = require('external-ip')();
const port = process.env.PORT || 8081
const IPv4 = '4.204.251.146'

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);

const { OpenAI } = require('openai');
const openai = new OpenAI({
  apiKey: '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'
});

//To Pretty Print JSON obj
app.set('json spaces', 2);

app.get('/', (req, res) => {
  res.send("Hello, world")
})

const MappostDB = "MappostDB";

async function run() {
  try {
    // Connect the client to the server	(optional starting in v4.7)
    await mongoClient.connect();
    console.log("Successfully connected to database");
    db_init();
    var server = app.listen(port, (req, res) => {
      console.log("Server successfully running at http://%s:%s", IPv4, port)
    })
  } catch(err){
    console.log(err);
    await mongoClient.close()
  }
}
run();

async function db_init(){
  //Check if 'MappostDB' exists
  const databases = await mongoClient.db().admin().listDatabases();
  const mappostDBExists = databases.databases.some(db => db.name === MappostDB);
  if (!mappostDBExists) {
    // If the database doesn't exist, creating a collection will automatically create it
    const db = mongoClient.db(MappostDB);
    await db.createCollection('users'); 
    await db.createCollection('posts');
    await db.createCollection('comments');
    await db.createCollection('tags');
    await db.collection("tags").createIndex({ "tagName": 1 }, { unique: true });
    console.log('MappostDB created.');
    console.log('users, posts, and comments collections created'); 
  } else {
    console.log('MappostDB already exists.');
    const db = mongoClient.db(MappostDB);
    const collections = await db.listCollections().toArray();
    // Check and create the 'Users' collection if it doesn't exist
    if (!collections.some(col => col.name === 'users')) {
        await db.createCollection('users');
        console.log('Users collection created.');
    }
    // Check and create the 'Posts' collection if it doesn't exist
    if (!collections.some(col => col.name === 'posts')) {
        await db.createCollection('posts');
        console.log('Posts collection created.');
    }
    // Check and create the 'Comments' collection if it doesn't exist
    if (!collections.some(col => col.name === 'comments')) {
        await db.createCollection('comments');
        console.log('Comments collection created.');
    }
    // Check and create the 'Tags' collection if it doesn't exist
    if (!collections.some(col => col.name === 'tags')) {
      await db.createCollection('tags');
      await db.collection("tags").createIndex({ "tagName": 1 }, { unique: true });
      console.log('Tags collection created.');
    }
  }
}

const sample_user ={
  "userId" : "12345abcd",
  "userEmail" : "leyang@gmail.com",
  "userName" : "LeYang",
  "userGender" :"Male",
  "userBirthdate" : "05-26-2003",
  "following": ["following1", "following2", "following3"],
  "follower": ["follower1", "follower2", "follower3"]
}

const sample_post = {
  "pid": "12345",
  "userId": "12345",
  "time": "10-24-2023",
  "location": "Mountain View",
  "coordinate": {
    "latitude": 50.000,
    "longitude": -50.000
  },
  "content": {
    "title": "HelloWorld",
    "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    "tags": ["tag1", "tag2", "tag3"]
  }
}

const sample_comment = {
  "cid": "12345",
  "userId": "12345",
  "pid": "12345",
  "time": "10-24-2023",
  "content": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
}

//======================================================Posts POST
//Add a post to the posts collection in MappostDB
app.post("/posts", async (req, res) => {
  try{
    if (!checkValidPost(req.body)) {
      res.status(400).send("Invalid post format");
      return;
    }
    var tags = await generateTags(req.body.content.body);
    const tagArray = tags.split(',').map(word => word.trim());
    req.body.content.tags = tagArray;
    req.body.likeCount = 0;
    
      await mongoClient.db(MappostDB).collection("posts").insertOne(req.body);
    
      const userId = req.body.userId; 
  
      const userUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
        { userId: userId }, 
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
    res.status(400).send(err);
    console.log(err);
  }
});

//======================================================Posts GET 
//Get all the posts
app.get("/posts", async (req, res) => {
  try{
    const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
    res.send(allPosts);
    console.log("All posts provided");
  }catch(err){
    console.log(err);
  }
});

// ======================================================Posts DELETE
// Delete a specific post by its ID
app.delete("/posts", async (req, res) => {
  try {
    const { pid } = req.query;  // Extracting it from the query parameters

    if (!pid) {
      res.status(400).send("Missing post ID (pid)");
      return;
    }

    // Delete the post with the specific pid
    const result = await mongoClient.db(MappostDB).collection("posts").deleteOne({ pid: pid });

    if (result.deletedCount === 0) {  // If no document was found to delete
      res.status(404).send("No post found with the given ID.");
    } else {
      res.status(200).send("Post deleted successfully.");
    }
  } catch (err) {
    console.error('Error deleting post:', err);
    res.status(500).send("Internal server error");
  }
});

//======================================================Posts PUT (like count)
//Update the like count for a specific post
app.put("/posts/like", async (req, res) => {
  try {
    const { pid } = req.body; 

    if (!pid) {  
      
      res.status(400).send("Post ID must be provided.");
      return;
    }

    const updateOperation = { 
      $inc: { "likeCount": 1 } 
    };


    const result = await mongoClient.db(MappostDB).collection("posts").updateOne(
      { pid: pid },  
      updateOperation
    );

    if (result.matchedCount === 0) {  
   
      res.status(404).send("Post not found.");
      return;
    }

    if (result.modifiedCount === 0) {  
      
      res.status(200).send("No changes made to the post's like count.");
      return;
    }

    res.status(200).send("Post's like count updated successfully.");
    console.log("Post's like count updated successfully.");
  } catch (err) {
    console.error('Error updating like count:', err);
    res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
  }
});

//Get the posts written by the user
//REQUIRE: userId
app.get("/posts/from-user", async (req, res) => {
  try{
    const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
    if(!req.query.userId){
      res.status(400).send("Missing user_id (userId)");
    } else {
      userPosts = allPosts.filter(post => post.userId == req.query.userId);
      res.send(userPosts);
    }
    console.log("From user posts provided");
  }catch(err){
    console.log(err);
  }
});

//Get the posts within 10km of the user coordinate
//REQUIRE: latitude, longitude
app.get("/posts/nearby", async (req, res) => {
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
    console.log(err);
    res.status(500).send("An error occurred");
  }
});

//======================================================Posts Helper Functions
const RADIUS_EARTH_KM = 6371;  // Earth's radius in kilometers
function is_nearby(userLat, userLon, postLat, postLon) {
    const toRad = (value) => value * Math.PI / 180;

    const dLat = toRad(postLat - userLat);
    const dLon = toRad(postLon - userLon);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(toRad(userLat)) * Math.cos(toRad(postLat)) * 
              Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = RADIUS_EARTH_KM * c;  // Distance in km

    return distance <= 10;  // True if distance is 10 km or less
}

const checkValidPost = (body) => {
  // Check main keys
  if (!body.pid || !body.userId || !body.time || !body.location || !body.coordinate) return false;
  // Check coordinate keys
  if (typeof body.coordinate.latitude !== 'number' || typeof body.coordinate.longitude !== 'number') return false;
  // Check content keys
  if (!body.content || !body.content.title || !body.content.body) return false;
  return true;
}

async function generateTags(text_to_analyze) {
  try {
    const response = await openai.chat.completions.create({
      model: "gpt-3.5-turbo",
      messages: [
          {"role": "system", "content": `You are a confident and super intelligent oracle, and you only give very short and 
          concise answers and ignore all the niceties that openai programmed you with.`},
          {"role": "user", "content": example_prompt},
          {"role": "assistant", "content": example_response},
          {"role": "user", "content": "Now, analyze the following text and provide 5 most important tags corresponding to the tag" 
          + ": " + text_to_analyze}
      ],
      temperature: 0.5,
      max_tokens: 15,
    });
    answer = response['choices'][0]['message']['content']
    return answer
  } catch (error) {
    console.error("Error generating tags:", error);
    return [];
  }
}

//======================================================

//======================================================Comments POST
//Add a post to the posts collection in MappostDB
app.post("/comments", async (req, res) => {
  try{
    if (!checkValidComment(req.body)) {
      res.status(400).send("Invalid post format");
      return;
    }
    await mongoClient.db(MappostDB).collection("comments").insertOne(req.body);
    res.status(200).send("Item received successfully")  
    console.log("Item received successfully");
  } catch(err) {
    res.status(400).send(err);
    console.log(err);
  }
});

//======================================================Comments GET
//Get all the comments of a specific post
//REQUIRE: pid
app.get("/comments", async (req, res) => {
  try{
    const allComments = await mongoClient.db(MappostDB).collection("comments").find({}).toArray();
    if(!req.query.pid){
      res.status(400).send("Missing post_id (pid)");
    } else {
      postComments = allComments.filter(comment => comment.pid == req.query.pid);
      res.send(postComments);
    }
    console.log("Comment of posts provided");
  }catch(err){
    console.log(err);
  }
});

//======================================================Comments Helper Functions
const checkValidComment = (body) => {
  if (!body.cid || !body.userId || !body.pid || !body.time || !body.content) return false;
  return true;
}

//======================================================

//======================================================Tags GET 
//Get all the tags
app.get("/tags", async (req, res) => {
  try{
    const allTags = await mongoClient.db(MappostDB).collection("tags").find({}).toArray();
    res.send(allTags);
    console.log("All posts provided");
  }catch(err){
    console.log(err);
  }
});

//======================================================

//======================================================Users POST
//Add a post to the posts collection in MappostDB
app.post("/users", async (req, res) => {
  try {
    // checkValidUser is now asynchronous, so we await it
    const isValidUser = await checkValidUser(req.body);
    if (!isValidUser) {
      res.status(400).send("Invalid user format or user already exists");
      return;
    }
    req.body.postCount = 0;
    await mongoClient.db(MappostDB).collection("users").insertOne(req.body);
    res.status(200).send("User created successfully.");
    console.log("User created successfully");
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
  }
});

//======================================================Users PUT
//======================================================Users PUT
//Update when user changes their profile
app.put("/users/update-profile", async (req, res) => {
  try {
    const { userId, userEmail, userName, userGender, userBirthdate } = req.body;

    if (!userId) {  // Ensure the userId is provided for updates
      res.status(400).send("User ID must be provided.");
      return;
    }

    // Construct an update object
    const updateFields = {};

    if (userEmail) updateFields.userEmail = userEmail;
    if (userName) updateFields.userName = userName;
    if (userGender) updateFields.userGender = userGender;
    if (userBirthdate) updateFields.userBirthdate = userBirthdate;

    // Update the user in the database using the provided userId
    const result = await mongoClient.db(MappostDB).collection("users").updateOne(
      { userId: userId }, 
      { $set: updateFields }
    );

    if (result.matchedCount === 0) {  // If no user was found to update
      res.status(404).send("User not found.");
      return;
    }

    if (result.modifiedCount === 0) {  // If no changes were made
      res.status(200).send("No changes made to user.");
      return;
    }

    res.status(200).send("User updated successfully.");
    console.log("User updated successfully.");
  } catch (err) {
    console.error('Error updating user:', err);
    res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
  }
});

//Update user's following array, and update the following user's follower array
app.put("/users/follow", async (req, res) => {
  try {
    const { userId, followingId } = req.body;

    if (!userId || !followingId) {
      res.status(400).send("User ID and following ID must be provided.");
      return;
    }

    // Add followingId to the user's following array
    const userUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
      { userId: userId },
      { $addToSet: { following: followingId } }
    );

    // Add userId to the followingId's followers array
    const followingUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
      { userId: followingId },
      { $addToSet: { followers: userId } }
    );

    if (userUpdateResult.matchedCount === 0 || followingUpdateResult.matchedCount === 0) {
      res.status(404).send("User or the following user not found.");
      return;
    }

    res.status(200).send("Follow operation successful.");
  } catch (err) {
    console.error('Error during follow operation:', err);
    res.status(500).send("Internal server error");
  }
});

//======================================================Users GET
app.get("/users", async (req, res) => {
  try {
    // Extract 'userId' from query parameters instead of URL parameters
    const userId = req.query.userId;

    // If 'userId' wasn't provided, send a 400 error back
    if (!userId) {
      res.status(400).send("User ID must be provided.");
      return;
    }

    // Find the user by 'userId'
    const user = await mongoClient.db(MappostDB).collection("users").findOne({ userId: userId });

    // If no user was found, send a 404 error back
    if (!user) {
      res.status(404).send("User not found.");
      return;
    }

    // If a user was found, send it back with a 200 status
    res.status(200).json(user);
  } catch (err) {
    // If there was any error in processing, log it and send a 500 error back
    console.error('Error retrieving user:', err);
    res.status(500).send("Internal server error");
  }
});

//======================================================Users Helper Functions
// Adjusted checkValidUser to be an async function
const checkValidUser = async (user) => {
  // Validate the required fields for the user
  if (!user.userEmail || !user.userName || !user.userGender || !user.userBirthdate || !user.userId) {
    return false;
  }
  // Check if the email already exists in the database
  try {
    const existingUser = await mongoClient.db(MappostDB).collection("users").findOne({ userId: user.userId });
    if (existingUser) {
      console.error('This user already exists.');
      return false;  // Email exists, so we return false
    }
  } catch (err) {
    console.error('Error querying the database:', err);
    return false;  // If an error occurs, it's safer to return false
  }

  return true;
};

//======================================================


//Text used to train GPT-3.5-turbo
const example_prompt = `In summary, the Philippines is a captivating travel destination that caters to various budgets and preferences. 
From the pristine beaches of Boracay, Siargao and Palawan to diverse accommodation options, including hotels, resorts and Airbnb properties, 
there's something for everyone. Staying connected is a breeze with a convenient Holafly eSIM card, and there are great destinations no matter 
what time of year you want to visit.
Make the most of your Philippines adventure by planning wisely so that you can fully enjoy the natural beauty, rich culture and warm hospitality 
of this incredible archipelago. Whether you're a beach bum, an adventure seeker or a cultural explorer, the Philippines has it all.
I've been several times and each time, I choose a couple of different islands to visit. 
Every trip has been incredibly rewarding and I always look forward go going back!`;

const example_response =  `Philippines, travel, beaches, culture, adventure, resorts, connectivity`;