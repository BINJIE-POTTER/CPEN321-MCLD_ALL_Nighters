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
  }
}

const sample_post = {
  "pid": "12345",
  "uid": "12345",
  "time": "10-24-2023",
  "location": "Mountain View",
  "coordinate": {
    "latitude": 50.000,
    "longitude": -50.000
  },
  "content": {
    "title": "HelloWorld",
    "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
  }
}

const sample_user ={
 "uid" : "12345",
 "email" : "leyang@gmail.com",
 "name" : "LeYang",
 "gender" :"Male",
 "age" : "34"

}

//======================================================Posts POST
//Add a post to the posts collection in MappostDB
app.post("/posts", async (req, res) => {
  try{
    if (!checkValidPost(req.body)) {
      res.status(400).send("Invalid post format");
      return;
    }
    await mongoClient.db(MappostDB).collection("posts").insertOne(req.body);
    res.status(200).send("Item received successfully")  
    console.log("Item received successfully");
  } catch(err) {
    res.status(400).send(err);
    console.log(err);
  }
});

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

    await mongoClient.db(MappostDB).collection("users").insertOne(req.body);
    res.status(200).send("User created successfully");
    console.log("User created successfully");
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).send("Internal server error");  // Adjust based on your error handling strategy
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

//======================================================Users GET 
//Get the user ID based on email
app.get("/users", async (req, res) => {
  try {
    // Get email from query params (e.g., "/users?email=user@example.com")
    const { email } = req.query;

    if (!email) {
      res.status(400).send("Email query parameter is required");
      return;
    }

    // Find one user by email and project only the 'uid' field
    const user = await mongoClient.db(MappostDB).collection("users").findOne({ email: email }, { projection: { uid: 1, _id: 0 } });

    if (!user) {
      res.status(404).send("User not found");
      return;
    }

    // If user found, return the user ID (assumed to be the 'uid' field in your documents)
    res.json({ uid: user.uid }); // Sending the 'uid' as part of a JSON response
    console.log("User ID provided");
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal Server Error");
  }
});

//Get the posts written by the user
//REQUIRE: uid
app.get("/posts/from-user", async (req, res) => {
  try{
    const allPosts = await mongoClient.db(MappostDB).collection("posts").find({}).toArray();
    if(!req.query.uid){
      res.status(400).send("Missing user_id (uid)");
    } else {
      userPosts = allPosts.filter(post => post.uid == req.query.uid);
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
  if (!body.pid || !body.uid || !body.time || !body.location || !body.coordinate) return false;
  // Check coordinate keys
  if (typeof body.coordinate.latitude !== 'number' || typeof body.coordinate.longitude !== 'number') return false;
  // Check content keys (only title is mandatory)
  if (!body.content || !body.content.title) return false;
  return true;
}

// Adjusted checkValidUser to be an async function
const checkValidUser = async (user) => {
  // Validate the required fields for the user
  if (!user.uid || !user.email || !user.name || !user.gender || !user.age) {
    return false;
  }

  // Check if the email already exists in the database
  try {
    const existingUser = await mongoClient.db(MappostDB).collection("users").findOne({ email: user.email });
    if (existingUser) {
      console.error('A user with this email already exists.');
      return false;  // Email exists, so we return false
    }
  } catch (err) {
    console.error('Error querying the database:', err);
    return false;  // If an error occurs, it's safer to return false
  }

  return true;
};

// //Extract data from todolist
// app.get("/todolist", async (req, res) => {
//   try{
//     const result = await mongoClient.db("test").collection("todolist").find(req.body).toArray();
//     res.send(result);
//     console.log("Todo items provided");
//   }catch(err){
//     console.log(err);
//   }
// });

// //Add a new data into todolist
// app.post("/todolist", async (req, res) => {
//   try{
//     await mongoClient.db("test").collection("todolist").insertOne(req.body);
//     res.status(200).send("Item received successfully")  
//     console.log("Item received successfully");
//   }catch(err){
//     res.status(400).send(err);
//     console.log(err);
//   }
// });

// //Update an existing data with the header "Finish this test"
// app.put("/todolist", async (req, res) => {
//   try{
//     await mongoClient.db("test").collection("todolist").replaceOne({"task": "Finish this test"}, req.body);
//     res.status(200).send("Item replaced successfully")  
//     console.log("Item replaced successfully");
//   }catch(err){
//     res.status(400).send(err);
//     console.log(err);
//   }
// });

// //Remove a data with the specific body
// app.delete("/todolist", async (req, res) => {
//   try{
//     await mongoClient.db("test").collection("todolist").deleteOne({"task": req.body.task});
//     res.status(200).send("Item deleted successfully")  
//     console.log("Item deleted successfully");
//   }catch(err){
//     res.status(400).send(err);
//     console.log(err);
//   }
// });