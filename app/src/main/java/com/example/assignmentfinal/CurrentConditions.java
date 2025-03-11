package com.example.assignmentfinal;

public class CurrentConditions {
    String cityName;
    double temp;
    double feelsLike;
    String conditions;
    Double cloudcover;
    double windDir;
    double windSpeed;
    double windGust;
    double humidity;
    double uvIndex;
    double visibility;
    long sunriseEpoch;
    long sunsetEpoch;
    String icon;

    public CurrentConditions(String cityName, double temp, double feelsLike, String conditions,
                             Double cloudcover, double windDir, double windSpeed, double windGust,
                             double humidity, double uvIndex, double visibility, long sunriseEpoch,
                             long sunsetEpoch, String icon) {

        this.cityName = cityName;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.conditions = conditions;
        this.cloudcover = cloudcover;
        this.windDir = windDir;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.humidity = humidity;
        this.uvIndex = uvIndex;
        this.visibility = visibility;
        this.sunriseEpoch = sunriseEpoch;
        this.sunsetEpoch = sunsetEpoch;
        this.icon = icon;
    }
}
