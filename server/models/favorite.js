const mongoose = require("mongoose");

const Schema = mongoose.Schema;

const FavoriteSchema = new Schema({
  country: String,
  lat: Number,
  lon: Number,
  name: String,
  region: String,
  tz_id: String
});

const Favorite = mongoose.model('Favorite', FavoriteSchema);

module.exports = Favorite;
