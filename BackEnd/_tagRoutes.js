const express = require('express');
const router = express.Router();
const postsHelper = require('./_postRoutes.js');

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";

//======================================================Tags GET 
//ChatGPT usage: No
//Get all the tags
//While it's true that this particular try...catch block doesn't handle errors in a sophisticated way 
//(e.g., providing custom error responses to the client), 
//having the catch block in place can still be useful for debugging and logging purposes. 
//It allows us to catch and log any unexpected errors that might occur during the database query or response sending process.
router.get("/tags", async (req, res) => {
  try{
    const allTags = await mongoClient.db(MappostDB).collection("tags").find({}).toArray();
    res.status(200).send(allTags);
    console.log("All posts provided");
  }catch(err){
    console.error("Internal Server Error");
    res.status(500).send("Internal Server Error");
  }
});

//ChatGPT usage: Partial
//Get nearby tags
router.get("/tags/nearby", async (req, res) => {
  try {
    const user_latitude = parseFloat(req.query.latitude);
    const user_longitude = parseFloat(req.query.longitude);
    if (!user_latitude || !user_longitude) {
      res.status(400).send("Missing user latitude or longitude");
      return;
    }
  
    // Filter posts based on the is_nearby function.
    const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
    const nearbyPosts = allPosts.filter(post => 
      postsHelper.is_nearby(user_latitude, user_longitude, post.coordinate.latitude, post.coordinate.longitude)
    );
  
    // Extract tags from nearbyPosts
    let allTags = [];
    nearbyPosts.forEach(post => {
      allTags = allTags.concat(post.content.tags);
    });
  
    // Filter out duplicates
    const uniqueTags = [...new Set(allTags)];
  
    console.log("Nearby tags successfully sent");
    res.status(200).send(uniqueTags);
    
  } catch (err) {
    console.log("Internal Server Error");
    res.status(500).send("Internal Server Error");
  }
});

module.exports = router;