package com.example.weather.data

class SuggestedActivitiesRepository {
    // HashMap to map weather conditions to a list of suggested activities
    private val suggestedActivities = hashMapOf<String, List<String>>()

    init {
        suggestedActivities["Sunny"] = arrayListOf("Go hiking!", "City walk in a dress!", "Air quality is good today.")
        suggestedActivities["Rainy"] = arrayListOf("Visit a museum.", "Stay indoors and read a book.", "Carry an umbrella!")
        suggestedActivities["Snowy"] = arrayListOf("Skiing if near a ski resort.", "Build a snowman!", "Wear warm clothes.")
        suggestedActivities["Cloudy"] = arrayListOf("Go for a photography walk.", "Visit a local café.", "Light may be dim, good for soft photographs.")
        suggestedActivities["Partly cloudy"] = arrayListOf("Go for a photography walk.", "Visit a local café.", "Light may be dim, good for soft photographs.")
        suggestedActivities["Clear"] = arrayListOf("Go for a photography walk.", "Visit a local café.", "Light may be dim, good for soft photographs.")
        suggestedActivities["Windy"] = arrayListOf("Fly a kite.", "Try windsurfing if near water.", "Watch out for flying debris.")
        suggestedActivities["Overcast"]  = arrayListOf("Enjoy a film marathon.", "Visit indoor historical sites.", "Use soft lighting for indoor photography.")
        suggestedActivities["Mist"] = arrayListOf("Take a reflective walk.", "Photography in misty conditions.", "Drive with caution.")
        suggestedActivities["Patchy rain possible"] = arrayListOf("Carry a portable umbrella.", "Enjoy a cozy read at a café.", "Prepare for sudden weather changes.")
        suggestedActivities["Blizzard"] = arrayListOf("Stay indoors if possible.", "Check emergency supplies.", "Keep warm with indoor activities.")
        suggestedActivities["Fog"] = arrayListOf("Low-speed driving with fog lights.", "Enjoy the eerie landscape on a safe walk.", "Photography for moody scenes.")
        suggestedActivities["Heavy rain"] = arrayListOf("Indoor water aerobics.", "Watch rain-themed movies.", "Bake something warm.")
        suggestedActivities["Patchy light snow"] = arrayListOf("Light outdoor photography.", "Short walks in the snow.", "Gentle snowball play.")
        suggestedActivities["Moderate or heavy snow showers"] = arrayListOf("Build an elaborate snowman.", "Skiing if conditions allow.", "Snow fort building.")
        suggestedActivities["Torrential rain shower"] = arrayListOf("Stay indoors—perfect for movie watching.", "Check for local flooding updates.", "Engage in indoor gardening.")
    }

    // If the type is found in the map, it will return the list of activities or it will return null.
    fun getSuggestedActivitiesByType(type: String): List<String>? {
        return suggestedActivities.get(type);
    }
}