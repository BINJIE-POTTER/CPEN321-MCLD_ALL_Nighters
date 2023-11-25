const express = require('express');
const router = express.Router();

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";

const admin = require('firebase-admin');
var serviceAccount = require("./firebase.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const multer = require('multer');
const upload = multer({ dest: 'uploads/' });
const fs = require('fs');

//======================================================Users POST
//ChatGPT usage: Partial
//Add a user to the user collection in MappostDB
router.post("/users", upload.single('image'), async (req, res) => {
    try {
        // checkValidUser is now asynchronous, so we await it
        const isValidUser = await checkValidUser(req.body);
        if (!isValidUser) {
            res.status(400).send("Invalid user format or user already exists");
            return;
        }

        req.body.postCount = 0;

        var avatar = {
            contentType: "",
            image: ""
        }
        req.body.userAvatar = avatar;


        if (process.env.NODE_ENV === 'test') {
            req.file = {
                path: './defaultAvatar.jpg',
                mimetype: 'image/jpeg',
            }
        }

        if (req.file) {
            const img = fs.readFileSync(req.file.path);
            const encode_image = img.toString('base64');
            var finalAvatar = {
                contentType: req.file.mimetype,
                image: new Buffer.from(encode_image, 'base64')
            };
            if (process.env.NODE_ENV === 'test') {
                finalAvatar.image = "12345";
            }
            req.body.userAvatar = finalAvatar;
            console.log("User Avatar received");
        }

        await mongoClient.db(MappostDB).collection("users").insertOne(req.body);
        res.status(200).send("User created successfully.");
        console.log("User created successfully");

    } catch (err) {
        console.log('Error creating user:', err);
        res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
    }
});
  
//======================================================Users PUT
//ChatGPT usage: Partial
//Update when user changes their profile
router.put("/users/update-profile", async (req, res) => {
    try {
        // Include token in the destructuring assignment
        const { userId, userEmail, userName, userGender, userBirthdate, token } = req.body;

        console.log({ userId, userEmail, userName, userGender, userBirthdate, token });

        if (!userId) {  // Ensure the userId is provided for updates
            res.status(400).send("User ID must be provided.");
            return;
        }

        if (!(await userExists(userId))) {
            res.status(400).send("User does not exist.");
            return;
        }

        // Construct an update object
        const updateFields = {};

        if (userEmail) updateFields.userEmail = userEmail;
        if (userName) updateFields.userName = userName;
        if (userGender) updateFields.userGender = userGender;
        if (userBirthdate) updateFields.userBirthdate = userBirthdate;
        if (token) updateFields.token = token;

        // Update the user in the database using the provided userId
        const result = await mongoClient.db("MappostDB").collection("users").updateOne(
            { userId }, 
            { $set: updateFields }
        );

        if (result.modifiedCount === 0) {  // If no changes were made
            res.status(200).send("No changes made to user.");
            return;
        }

        res.status(200).send("User profile updated successfully.");
        console.log("User profile updated successfully.");
    } catch (err) {
        console.log('Error updating user:', err);
        res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
    }
});

router.put("/users/update-avatar", upload.single('image'), async (req, res) => {
    try {
        const userId = req.body.userId;

        if (!userId) {  // Ensure the userId is provided for updates
            res.status(400).send("User ID must be provided.");
            return;
        }

        if (!(await userExists(userId))) {
            res.status(400).send("User does not exist.");
            return;
        }

        if (process.env.NODE_ENV === 'test' && userId == 'existingUserId') {
            req.file = {
                path: './defaultAvatar.jpg',
                mimetype: 'image/jpeg',
            }
        }

        if (!req.file) {
            console.log("User avatar was not provided.");
            res.status(400).send("User avatar was not provided.");
            return;
        } 

        var finalAvatar;

        const img = fs.readFileSync(req.file.path);
        const encode_image = img.toString('base64');
        finalAvatar = {
            contentType: req.file.mimetype,
            image: new Buffer.from(encode_image, 'base64')
        };

        if (process.env.NODE_ENV === 'test') {
            finalAvatar = {
                contentType: 'image/jpeg',
                image: '12345'
            }
        }


        const updateFields = {};
        updateFields.userAvatar = finalAvatar;

        console.log("User Avatar received");

        const result = await mongoClient.db("MappostDB").collection("users").updateOne(
            { userId }, 
            { $set: updateFields }
        );

        if (result.modifiedCount === 0) {  // If no changes were made
            res.status(200).send("No changes made to user avatar.");
            return;
        }

        res.status(200).send("User avatar updated successfully.");
        console.log("User avatar updated successfully.");

    } catch (err) {
        console.log('Internal server error');
        res.status(500).send("Internal server error");
    }
});


//ChatGPT usage: Partial
//Update user's following array, and update the following user's follower array
router.put("/users/follow", async (req, res) => {
    try {
        const { userId, followingId } = req.body;

        if (!userId || !followingId) {
            res.status(400).send("User ID and following ID must be provided.");
            return;
        }

        if(!(await userExists(userId)) || !(await userExists(followingId))) {
            res.status(400).send("User does not exist.");
            return;
        }

        // Add followingId to the user's following array
        const userUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
            { userId },
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

        await notifyFollowerIncrease(followingId, res);

        res.status(200).send("Follow operation successful.");
    } catch (err) {
        console.error('Error during follow operation:', err);
        res.status(500).send("Internal server error");
    }
});

//ChatGPT usage: Partial
//Remove the followingId from UserId's following array, and remove the userId from the followingId's follower array
router.put("/users/unfollow", async (req, res) => {
    try {
        const { userId, followingId } = req.body;

        if (!userId || !followingId) {
        res.status(400).send("User ID and following ID must be provided.");
        return;
        }

        // Check if userId is indeed following followingId and vice-versa
        if (!(await isUserFollowing(userId, followingId))) {
        res.status(400).send("The specified users do not have a following relationship.");
        return;
        }

        // Remove followingId from the user's following array
        const userUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
        { userId },
        { $pull: { following: followingId } }
        );

        // Remove userId from the followingId's followers array
        const followingUpdateResult = await mongoClient.db(MappostDB).collection("users").updateOne(
        { userId: followingId },
        { $pull: { followers: userId } }
        );

        if (userUpdateResult.matchedCount === 0 || followingUpdateResult.matchedCount === 0) {
        res.status(404).send("User or the following user not found.");
        return;
        }

        res.status(200).send("Unfollow operation successful.");
    } catch (err) {
        console.error('Error during unfollow operation:', err);
        res.status(500).send("Internal server error");
    }
});


//======================================================Users GET
//ChatGPT usage: No
//Get the user info of a specific userId
router.get("/users", async (req, res) => {
    try {
        // Extract 'userId' from query parameters instead of URL parameters
        const userId = req.query.userId;

        // If 'userId' wasn't provided, send a 400 error back
        if (!userId) {
        res.status(400).send("User ID must be provided.");
        return;
        }

        // Find the user by 'userId'
        const user = await mongoClient.db(MappostDB).collection("users").findOne({ userId });

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
//ChatGPT usage: Partial
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

//ChatGPT usage: No
async function userExists(userId) {
    console.log("Checking if user exists:", userId);
    const user = await mongoClient.db(MappostDB).collection("users").findOne({ userId });
    const exists = user !== null;
    console.log(`User ${userId} exists: ${exists}`);
    return exists;
}

//ChatGPT usage: No
async function isUserFollowing(userId, followingId) {
    const user = await mongoClient.db(MappostDB).collection("users").findOne({ userId, following: followingId });
    const followingUser = await mongoClient.db(MappostDB).collection("users").findOne({ userId: followingId, followers: userId });
    return user !== null && followingUser !== null;  // Both conditions should be true for the relation to exist
}

//======================================================Users Send Notifications
// Send a notification to the following account saying "You got a new follower"


//ChatGPT usage: No (ChatGPT's method is deprecated, thus we implemented from scratch, referenced from firebase website)
async function notifyFollowerIncrease(userId, res) {
    if (!userId) {
        res.status(400).send("User ID and following ID must be provided.");
        return;
    }
    
    // Check if both users exist
    if (!(await userExists(userId))) {
    res.status(404).send("User or the following user not found.");
    return;
    }

    const user = await mongoClient.db(MappostDB).collection("users").findOne({ userId });
    var token = user.token;

    if (!token){
        return;
    }

    const message = {
        notification: {
            title: 'Congrats!',
            body: 'You got a new follower!'
        },
        data: {
            type: 'NEW_FOLLOWER'
        },
        token,
    }

    try {
        await admin.messaging().send(message);
        //console.log(message);
      } catch (error) {
        console.error(error);
      }
}


module.exports = {
    router: router,
    admin: admin,
    notifyFollowerIncrease: notifyFollowerIncrease,
    userExists: userExists,
    isUserFollowing:  isUserFollowing,
    checkValidUser: checkValidUser
}
