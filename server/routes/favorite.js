const express = require("express");
const Favorite = require("../models/favorite");
const { convertToXML } = require("../utils");
const router = express.Router();

// GET route to retrieve all favorite weather locations.
router.get("/", async (req, res) => {
  try {
    // Determine the response format (default to JSON).
    const format = req.query.format || "json";
    // Fetch all favorite locations from the database.
    const favorites = await Favorite.find({});

    if (format === "xml") {
      const xml = convertToXML(
        // Use the toJSON method to get a clean JSON object from the Mongoose model,
        // then convert it to XML with 'favorite' as the root element.
        favorites.map((f) => ({
          favorite: JSON.parse(JSON.stringify(f.toJSON())),
        })),
        "favorites"
      );
      res.header("Content-Type", "application/xml");
      res.send(xml);
    } else {
      res.json(favorites);
    }
  } catch (e) {
    res.status(500).send();
  }
});

// GET route to retrieve a single favorite location by its name.
router.get("/:name", async (req, res) => {
  try {
    const format = req.query.format || "json";
    // Fetch a single favorite location by name from the database.
    const favorite = await Favorite.findOne({ name: req.params.name });

    if (format === "xml") {
      const xml = convertToXML(
        JSON.parse(JSON.stringify(favorite.toJSON())),
        "favorite"
      );
      res.header("Content-Type", "application/xml");
      res.send(xml);
    } else {
      res.json(favorite);
    }
  } catch (e) {
    res.status(500).send();
  }
});

// The POST and DELETE routes do not return data and therefore do not require XML conversion.

// POST route to add a new favorite location.
router.post("/", async (req, res) => {
  try {
    // Create a new favorite with the data provided in the request body.
    const newFavorite = new Favorite(req.body);
    // Save the new favorite to the database.
    const savedFavorite = await newFavorite.save();
    // Send a success response with the saved favorite.
    res.json(savedFavorite);
  } catch (e) {
    res.status(500).send();
  }
});

// DELETE route to remove a favorite location by its name.
router.delete("/:name", async (req, res) => {
  try {
    const result = await Favorite.findOneAndDelete({ name: req.params.name });
    if (result) {
      // If a favorite was found and deleted, send a success response.
      res.status(200).json({ message: "Favorite successfully deleted." });
    } else {
      // If no favorite was found with that name, send a 404 not found response.
      res.status(404).json({ message: "Favorite not found." });
    }
  } catch (e) {
    res.status(500).send();
  }
});

module.exports = router;
