package com.example.assignmentfinal;

import android.net.Uri;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignmentfinal.DailyActivity;
import com.example.assignmentfinal.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class visualCrossing {

    private static final String TAG = "VisualCrossingDownloader";
    private static MainActivity mainActivity;
    private static DailyActivity dailyActivity;
    private static final String weatherURL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String apiKey = "S6LWWPZXG46ZKX95DHEMD93EW";

    public static void makeApiRequest(String location, String unit, MainActivity mainActivityIn) {

        mainActivity = mainActivityIn;

        // Build the URL for the API request
        Uri.Builder buildURL = Uri.parse(weatherURL).buildUpon();
        buildURL.appendPath(location);
        // Add the unit parameter
        buildURL.appendQueryParameter("unitGroup", unit);
        buildURL.appendQueryParameter("key", apiKey);

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doSearch: " + urlToUse);
        Response.Listener<JSONObject> listener = response -> parseJSON(response.toString());
        Response.ErrorListener error = error1 -> {
            if (error1.networkResponse != null) {
                int statusCode = error1.networkResponse.statusCode;

                if (statusCode == 400) {
                    // Handle incorrect location error
                    mainActivity.showLocationNotFoundError();
                } else if (statusCode >= 500) {
                    // Handle server error
                    mainActivity.showServerError();
                } else {
                    // Handle other errors
                    mainActivity.showerror();
                }
            } else {
                // Handle network error (e.g., no connectivity)
                mainActivity.showServerError();
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        Volley.newRequestQueue(mainActivity).add(jsonObjectRequest);

    }

    public static void doForecast(String location, String unit, DailyActivity dailyActivityIn) {

        dailyActivity = dailyActivityIn;

        // Build the URL for the API request
        Uri.Builder buildURL = Uri.parse(weatherURL).buildUpon();
        buildURL.appendPath(location);
        // Add the unit parameter
        buildURL.appendQueryParameter("unitGroup", unit);
        buildURL.appendQueryParameter("key", apiKey);

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doForecast: " + urlToUse);
        Response.Listener<JSONObject> listener = response -> parseDailyJSON(response.toString());
        Response.ErrorListener error = error1 -> dailyActivity.addDayForecast(null);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        Volley.newRequestQueue(dailyActivity).add(jsonObjectRequest);

    }


    private static void parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

            // Extract latitude and longitude
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");

            // Extract currentConditions and other necessary fields
            String resolvedAddress = jsonObject.getString("resolvedAddress");
            String cityName = resolvedAddress.split(",")[0].trim();  // Get the city name

            JSONObject currentConditions = jsonObject.getJSONObject("currentConditions");
            double currentTemp = currentConditions.getDouble("temp");
            double currentFeelslike = currentConditions.getDouble("feelslike");
            double currentHumidity = currentConditions.getDouble("humidity");
            double currentWindgust = currentConditions.has("windgust") && !currentConditions.isNull("windgust")
                    ? currentConditions.getDouble("windgust")
                    : 0; // Default value if windgust is null
            double currentWindspeed = currentConditions.has("windspeed") && !currentConditions.isNull("windspeed")
                    ? currentConditions.getDouble("windspeed")
                    : 0;
            double currentWinddir = currentConditions.getDouble("winddir");
            double currentVisibility = currentConditions.getDouble("visibility");
            double currentCloudcover = currentConditions.getDouble("cloudcover");
            double currentUvindex = currentConditions.getDouble("uvindex");
            String currentConditionsDesc = currentConditions.getString("conditions");
            String currentIcon = currentConditions.getString("icon");
            long sunriseEpoch = currentConditions.getLong("sunriseEpoch");
            long sunsetEpoch = currentConditions.getLong("sunsetEpoch");

            // Create CurrentConditions object
            CurrentConditions current = new CurrentConditions(
                    cityName, currentTemp, currentFeelslike, currentConditionsDesc, currentCloudcover,
                    currentWinddir, currentWindspeed, currentWindgust, currentHumidity, currentUvindex,
                    currentVisibility, sunriseEpoch, sunsetEpoch, currentIcon
            );


            // Extract days array
            JSONArray daysArray = jsonObject.getJSONArray("days");
            List<Daily> dayForecasts = new ArrayList<>();
            List<Hourly> tempHourForecasts = new ArrayList<>();
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject dayObject = daysArray.getJSONObject(i);
                long datetimeEpoch = dayObject.getLong("datetimeEpoch");
                double tempMax = dayObject.getDouble("tempmax");
                double tempMin = dayObject.getDouble("tempmin");
                double precipProb = dayObject.getDouble("precipprob");
                double uvindex = dayObject.getDouble("uvindex");
                String conditions = dayObject.getString("conditions");
                String description = dayObject.getString("description");
                String icon = dayObject.getString("icon");

                // Extract hours array within days
                JSONArray hoursArray = dayObject.getJSONArray("hours");
                for (int j = 0; j < hoursArray.length(); j++) {
                    JSONObject hourObject = hoursArray.getJSONObject(j);
                    String hourDatetime = hourObject.getString("datetime");
                    long hourDatetimeEpoch = hourObject.getLong("datetimeEpoch");
                    double temp = hourObject.getDouble("temp");
                    double feelsLike = hourObject.getDouble("feelslike");
                    double humidity = hourObject.getDouble("humidity");
                    double windGust = hourObject.has("windgust") && !hourObject.isNull("windgust")
                            ? hourObject.getDouble("windgust")
                            : 0; // Default value if windgust is null
                    double windSpeed = hourObject.has("windspeed") && !hourObject.isNull("windspeed")
                            ? hourObject.getDouble("windspeed")
                            : 0;
                    double windDir = hourObject.getDouble("winddir");
                    double visibility = hourObject.getDouble("visibility");
                    double cloudCover = hourObject.getDouble("cloudcover");
                    double uvIndex = hourObject.getDouble("uvindex");
                    String hourConditions = hourObject.getString("conditions");
                    String hourIcon = hourObject.getString("icon");

                    // Add each hour forecast to the list
                    tempHourForecasts.add(new Hourly(hourDatetime, hourDatetimeEpoch, temp, feelsLike,
                            humidity, windGust, windSpeed, windDir, visibility, cloudCover, uvIndex, hourConditions, hourIcon));
                }

                // Add each day forecast to the list
                dayForecasts.add(new Daily(cityName, datetimeEpoch, tempMax, tempMin, precipProb, uvindex,
                        conditions, description, icon, tempHourForecasts));
            }

            mainActivity.updateData(current, dayForecasts);
            mainActivity.updateHourForecasts(tempHourForecasts);

            // Extract alerts array
            JSONArray alertsArray = jsonObject.getJSONArray("alerts");
            List<Alert> alerts = new ArrayList<>();
            for (int i = 0; i < alertsArray.length(); i++) {
                JSONObject alertObject = alertsArray.getJSONObject(i);
                String event = alertObject.getString("event");
                String headline = alertObject.getString("headline");
                String alertId = alertObject.getString("id");
                String alertDescription = alertObject.getString("description");

                // Add each alert to the list
                alerts.add(new Alert(event, headline, alertId, alertDescription));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            if (mainActivity != null) {
                mainActivity.updateData(null, null); // Inform MainActivity of failure
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage());
        }
    }


    private static void parseDailyJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);

            // Extract latitude and longitude
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");

            // Extract currentConditions and other necessary fields
            String resolvedAddress = jsonObject.getString("resolvedAddress");
            String cityName = resolvedAddress.split(",")[0].trim();  // Get the city name

            // Extract days array
            JSONArray daysArray = jsonObject.getJSONArray("days");
            List<Daily> dayForecasts = new ArrayList<>();
            List<Hourly> tempHourForecasts = new ArrayList<>();
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject dayObject = daysArray.getJSONObject(i);
                long datetimeEpoch = dayObject.getLong("datetimeEpoch");
                double tempMax = dayObject.getDouble("tempmax");
                double tempMin = dayObject.getDouble("tempmin");
                double precipProb = dayObject.getDouble("precipprob");
                double uvindex = dayObject.getDouble("uvindex");
                String conditions = dayObject.getString("conditions");
                String description = dayObject.getString("description");
                String icon = dayObject.getString("icon");

                // Extract hours array within days
                JSONArray hoursArray = dayObject.getJSONArray("hours");
                for (int j = 0; j < hoursArray.length(); j++) {
                    JSONObject hourObject = hoursArray.getJSONObject(j);
                    String hourDatetime = hourObject.getString("datetime");
                    long hourDatetimeEpoch = hourObject.getLong("datetimeEpoch");
                    double temp = hourObject.getDouble("temp");
                    double feelsLike = hourObject.getDouble("feelslike");
                    double humidity = hourObject.getDouble("humidity");
                    double windGust = hourObject.has("windgust") && !hourObject.isNull("windgust")
                            ? hourObject.getDouble("windgust")
                            : 0; // Default value if windgust is null
                    double windSpeed = hourObject.has("windspeed") && !hourObject.isNull("windspeed")
                            ? hourObject.getDouble("windspeed")
                            : 0;
                    double windDir = hourObject.getDouble("winddir");
                    double visibility = hourObject.getDouble("visibility");
                    double cloudCover = hourObject.getDouble("cloudcover");
                    double uvIndex = hourObject.getDouble("uvindex");
                    String hourConditions = hourObject.getString("conditions");
                    String hourIcon = hourObject.getString("icon");

                    // Add each hour forecast to the list
                    tempHourForecasts.add(new Hourly(hourDatetime, hourDatetimeEpoch, temp, feelsLike,
                            humidity, windGust, windSpeed, windDir, visibility, cloudCover, uvIndex, hourConditions, hourIcon));
                }

                // Add each day forecast to the list
                dayForecasts.add(new Daily(cityName, datetimeEpoch, tempMax, tempMin, precipProb, uvindex,
                        conditions, description, icon, tempHourForecasts));
            }

            dailyActivity.addDayForecast(dayForecasts);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            if (dailyActivity != null) {
                dailyActivity.addDayForecast(new ArrayList<>()); // Inform DailyActivity of failure
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage());
        }
    }


}
