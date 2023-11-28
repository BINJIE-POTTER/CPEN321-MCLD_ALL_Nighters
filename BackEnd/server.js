//Backend-server used to connect to mongodb
var express = require("express")
var app = express();
var fs = require('fs');
var http = require('http');
var https = require('https');

const userRoutes = require('./_userRoutes').router;
const postRoutes = require('./_postRoutes').router;
const commentRoutes = require('./_commentRoutes');
const tagRoutes = require('./_tagRoutes');

app.use(express.json());

app.use(userRoutes);
app.use(postRoutes);
app.use(commentRoutes);
app.use(tagRoutes);

//Set up http and https server

var privateKey;
var certificate;
var credentials;
var httpServer;
var httpsServer;

if (process.env.NODE_ENV !== 'test') {
  privateKey = fs.readFileSync('cert/nginx-selfsigned2.key', 'utf8');
  certificate = fs.readFileSync('cert/nginx-selfsigned2.crt', 'utf8');
  credentials = { key: privateKey, cert: certificate };

  httpServer = http.createServer(app);
  httpsServer = https.createServer(credentials, app);
}

const httpPort = 8081
const httpsPort = 3000
const IPv4 = '4.204.251.146'

const { MongoClient} = require('mongodb');
const uri = "mongodb://0.0.0.0:27017/";
const mongoClient = new MongoClient(uri);
const MappostDB = "MappostDB";

//To Pretty Print JSON obj
app.set('json spaces', 2);

app.get('/', (req, res) => {
  const host = req.headers.host;
  res.send(`This server is accessed via: ${host}`);
})

//ChatGPT usage: No
async function run() {
  //Startup the server and database
  try {
    // Connect the client to the server	(optional starting in v4.7)
    if (process.env.NODE_ENV !== 'test') {
      await mongoClient.connect();
      console.log("Successfully connected to database");
      await db_init();
    }

    if (process.env.NODE_ENV !== 'test') {
      httpServer.listen(httpPort, () => {
        console.log("Server successfully running at http://%s:%s", IPv4, httpPort)
      });
  
      httpsServer.listen(httpsPort, () => {
        console.log("The https server running on 3000");
      });
    }

  } catch(err){
    console.log(err);
    httpServer.close();
    httpsServer.close();
    await mongoClient.close();
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

module.exports = app;