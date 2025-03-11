package com.example.assignmentfinal;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Daily  {

    static String cityName;
    long datetimeEpoch; // This should represent the start of the day
    double tempMax;
    double tempMin;
    double precipProb;
    double uvindex;
    String conditions;
    String description;
    String icon;
    List<Hourly> hours; // List of hourly forecasts
    private static final String TAG = "DayForecast";

    public Daily(String cityName, long datetimeEpoch, double tempMax, double tempMin, double precipProb, double uvindex,
                       String conditions, String description, String icon, List<Hourly> hours) {
        Daily.cityName = cityName;
        this.datetimeEpoch = datetimeEpoch;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.precipProb = precipProb;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.description = description;
        this.icon = icon;
        this.hours = hours;
    }

    public String getDayNDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
        return sdf.format(new Date(datetimeEpoch * 1000));
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    public String getDayDescription() {
        return description; // Assuming description is already in the format you want
    }

    public String getDayPrecipProb() {
        return String.format(Locale.getDefault(), "%.0f%% precip.", precipProb);
    }

    public String getDayUVIndex() {
        return String.format("UV Index: %.1f", uvindex);
    }

    private double getTemperatureAtHour(int hour) {
        // Calculate the target day in milliseconds
        long targetDayMillis = datetimeEpoch * 1000; // Convert to milliseconds
        Double targetTemp = null;

        for (Hourly hourForecast : hours) {
            long forecastMillis = hourForecast.datetimeEpoch * 1000; // Convert to milliseconds
            Calendar forecastCalendar = Calendar.getInstance();
            forecastCalendar.setTimeInMillis(forecastMillis);

            int forecastHour = forecastCalendar.get(Calendar.HOUR_OF_DAY);
            long forecastDayMillis = forecastCalendar.getTimeInMillis(); // Get the full timestamp for comparison

            // Log the hour being checked and the corresponding temperature
            Log.d(TAG, "Checking hour: " + forecastHour + " with temperature: " + hourForecast.temp);

            // Compare just the day part of the timestamp
            if (forecastHour == hour &&
                    (forecastDayMillis / 86400000) == (targetDayMillis / 86400000)) { // Compare days
                return hourForecast.temp; // Return temperature
            }

            // Store the temperature if it's from the target day and the hour is not exactly matched
            if ((forecastDayMillis / 86400000) == (targetDayMillis / 86400000)) {
                if (targetTemp == null || Math.abs(forecastHour - hour) < Math.abs(forecastHour - targetTemp)) {
                    targetTemp = hourForecast.temp;
                }
            }
        }

        // Return the nearest temperature or a default value (e.g., 0.0)
        return targetTemp != null ? targetTemp : 0.0; // Change this to your desired fallback value
    }



    public double getDayMorningTemp() {
        return getTemperatureAtHour(8); // 8 AM
    }

    public double getDayAfternoonTemp() {
        return getTemperatureAtHour(13); // 1 PM
    }

    public double getDayEveningTemp() {
        return getTemperatureAtHour(17); // 5 PM
    }

    public double getDayNightTemp() {
        return getTemperatureAtHour(23); // 11 PM
    }

    public String getDayWeatherIcon() {
        return icon; // Assuming icon contains the URL or resource name
    }

    public double getTemp() {
        return (tempMax + tempMin) / 2;
    }

    public long getDatetimeEpoch() {
        return datetimeEpoch;
    }

    public String getDescription() {
        return description;

    }

    public String getIcon() {
        return icon;
    }

    public String getConditions() {
        return conditions;
    }

    public String getCityName() {
        return cityName;
    }

    public double getPrecipProb() {
        return precipProb;
    }

    public double getUvindex() {
        return uvindex;
    }

    public List<Hourly> getHours() {
        return hours;
    }

    public Object getUvIndex() {
        return uvindex;
    }

}
