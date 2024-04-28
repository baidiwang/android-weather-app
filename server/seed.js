const mongoose = require("mongoose");
const Favorite = require("./models/favorite");

const MONGODB_URL = process.env.MONGODB_URI;

const initialFavorites = [
  {
    country: "United States of America",
    lat: 40.71,
    lon: -74.01,
    name: "New York",
    region: "New York",
    tz_id: "America/New_York",
  },
  {
    country: "China",
    lat: 31.01,
    lon: 121.41,
    name: "Shanghai",
    region: "Shanghai",
    tz_id: "Asia/Shanghai",
  },
];

async function init() {
  await mongoose.connect(MONGODB_URL);

  await Favorite.deleteMany({});

  for (let i = 0; i < initialFavorites.length; i++) {
    const favorite = new Favorite(initialFavorites[i]);
    await favorite.save();
  }

  console.log("MongoDB Connected!");
}

init().then(() => {
  console.log("Data inserted");
});
