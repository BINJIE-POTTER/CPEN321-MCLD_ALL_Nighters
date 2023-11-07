//Backend-server used to connect to mongodb
var express = require("express")
const userRoutes = require('./_userRoutes');
const postRoutes = require('./_postRoutes').router;
const commentRoutes = require('./_commentRoutes');
const tagRoutes = require('./_tagRoutes');

var app = express();
app.use(express.json());

app.use(userRoutes);
app.use(postRoutes);
app.use(commentRoutes);
app.use(tagRoutes);

// const getPublicIp = require('external-ip')();
const port = 8081
const IPv4 = '4.204.251.146'

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";


//To Pretty Print JSON obj
app.set('json spaces', 2);

app.get('/', (req, res) => {
  res.send("Hello, world")
})

//ChatGPT usage: No
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

//ChatGPT usage: Partial
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
    console.log('users, posts, comments, and tags collections created'); 
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