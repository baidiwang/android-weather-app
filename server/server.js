const express = require("express");
const { connectMongoDB } = require("./database");
const cors = require("cors"); //use the cors middleware to set up the necessary headers that tell the browser it's safe to allow a web page from one origin (our Angular app) to access resources at another origin (our Node.js server).
const app = express();
require("dotenv").config();

// Import the routing files for the favorite and weather endpoints.
const favorite = require("./routes/favorite");
const weather = require("./routes/weather");

// Establish a connection to MongoDB using the connectMongoDB function.
connectMongoDB();

app.use(cors()); // Use CORS middleware to allow cross-origin requests from the frontend.The server and the client-side application run on different ports during development.
app.use(express.json()); // Use express.json() middleware to parse JSON payloads in incoming requests.

// Define the routes for the application.
app.use("/favorite", favorite);
app.use("/weather", weather);

// Start the server on port 8080
app.listen(8080, () => {
  console.log(`Server start http://localhost:8080`);
});
