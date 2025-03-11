package com.example.assignmentfinal;


import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Hourly {

    String datetime;
    long datetimeEpoch;
    double temp;
    double feelsLike;
    double humidity;
    double windGust;
    double windSpeed;
    double windDir;
    double visibility;
    double cloudCover;
    double uvIndex;
    String conditions;
    String icon;

    public Hourly(String datetime, long datetimeEpoch, double temp, double feelsLike, double humidity,
                        double windGust, double windSpeed, double windDir, double visibility, double cloudCover,
                        double uvIndex, String conditions, String icon) {
        this.datetime = datetime;
        this.datetimeEpoch = datetimeEpoch;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windGust = windGust;
        this.windSpeed = windSpeed;
        this.windDir = windDir;
        this.visibility = visibility;
        this.cloudCover = cloudCover;
        this.uvIndex = uvIndex;
        this.conditions = conditions;
        this.icon = icon;
    }

    public String getDatetime() {
        return datetime;
    }

    public double getTemp() {
        return temp;
    }

    public String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayName = sdf.format(new Date(datetimeEpoch * 1000));

        // Check if it's today
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Calendar forecastCalendar = Calendar.getInstance();
        forecastCalendar.setTimeInMillis(datetimeEpoch * 1000);

        if (calendar.get(Calendar.DAY_OF_YEAR) == forecastCalendar.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == forecastCalendar.get(Calendar.YEAR)) {
            return "Today";
        } else {
            return dayName; // Return the actual day name
        }
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(datetimeEpoch * 1000));
    }

    public String getTemperature() {
        return String.format(Locale.getDefault(), "%.0f°", temp);
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return conditions;
    }

    public String getWeatherIcon() {
        return icon;
    }

    public long getDatetimeEpoch() {
        return datetimeEpoch;
    }



    @Override
    public String toString() {
        return "HourForecast{" +
                "datetime='" + datetime + '\'' +
                ", datetimeEpoch=" + datetimeEpoch +
                ", temp=" + temp +
                ", feelsLike=" + feelsLike +
                ", humidity=" + humidity +
                ", windGust=" + windGust +
                ", windSpeed=" + windSpeed +
                ", windDir=" + windDir +
                ", visibility=" + visibility +
                ", cloudCover=" + cloudCover +
                ", uvIndex=" + uvIndex +
                ", conditions='" + conditions + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

}
