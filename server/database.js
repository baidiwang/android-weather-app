const mongoose = require("mongoose");

function connectMongoDB() {
  mongoose
    .connect(process.env.MONGODB_URI)
    .then(() => console.log("MongoDB Connected!"));
}

module.exports = {
  connectMongoDB,
};
