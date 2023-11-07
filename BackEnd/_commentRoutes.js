const express = require('express');
const router = express.Router();

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";

const { v4: uuidv4 } = require('uuid');


//======================================================Comments POST
//ChatGPT usage: No
//Add a comment to the comments collection in MappostDB
router.post("/comments", async (req, res) => {
    try{
        if (!checkValidComment(req.body)) {
            res.status(400).send("Invalid comment format");
            return;
        }
  
        const uniqueCommentId = uuidv4();
        req.body.cid = uniqueCommentId;
  
        await mongoClient.db(MappostDB).collection("comments").insertOne(req.body);
        res.status(200).send("Item received successfully")
        console.log("Item received successfully");
    } catch(err) {
        res.status(500).send(err);
        console.log(err);
    }
});
  
//======================================================Comments GET
//ChatGPT usage: No
//Get all the comments of a specific post
//REQUIRE: pid
router.get("/comments", async (req, res) => {
    try{
        const allComments = await mongoClient.db(MappostDB).collection("comments").find({}).toArray();
        if(!req.query.pid){
            res.status(400).send("Missing post_id (pid)");
        } else {
            const postComments = allComments.filter(comment => comment.pid == req.query.pid);
            res.send(postComments);
        }
        console.log("Comment of posts provided");
    }catch(err){
        console.log(err);
    }
});
  

//======================================================Comments DELETE
//ChatGPT usage: Partial
//Delete a specific comment from a post in the MappostDB
//REQUIRE: cid, pid, userId
router.delete("/comments", async (req, res) => {
    try {
        const { cid, pid, userId } = req.query;
  
        if (!cid || !pid || !userId) {
            res.status(400).send("Missing required parameters: cid, pid, or userId");
            return;
        }
  
        const comment = await mongoClient.db(MappostDB).collection("comments").findOne({ cid, pid });
  
        if (!comment) {
            res.status(404).send("Comment not found");
            return;
        }
  
  
        if (comment.userId !== userId) {
            res.status(403).send("User is not authorized to delete this comment");
            return;
        }
  
        const deleteResult = await mongoClient.db(MappostDB).collection("comments").deleteOne({ cid, pid});
  
        if (deleteResult.deletedCount === 0) {
            throw new Error("Failed to delete the comment");
        }
  
        res.status(200).send("Comment deleted successfully");
        console.log("Comment deleted");
    } catch (err) {
        res.status(500).send(err.toString());
        console.log(err);
    }
  });
  
//======================================================Comments Helper Functions
//ChatGPT usage: No
const checkValidComment = (body) => {
    if ( !body.userId || !body.pid || !body.time || !body.content) return false;
    return true;
}
  


module.exports = router;
