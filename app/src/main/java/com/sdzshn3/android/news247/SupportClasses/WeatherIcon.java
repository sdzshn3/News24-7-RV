package com.sdzshn3.android.news247.SupportClasses;

import com.sdzshn3.android.news247.R;

public class WeatherIcon {
    public static int getWeatherIcon(String iconId) {
        int resource;
        switch (iconId) {
            case "11d":
                resource = R.drawable.thunder_day;
                break;
            case "11n":
                resource = R.drawable.thunder_night;
                break;
            case "09d":
                resource = R.drawable.rainy_weather;
                break;
            case "09n":
                resource = R.drawable.rainy_night;
                break;
            case "10d":
                resource = R.drawable.rainy_day;
                break;
            case "10n":
                resource = R.drawable.rainy_night;
                break;
            case "13d":
                resource = R.drawable.rain_snow;
                break;
            case "13n":
                resource = R.drawable.rain_snow_night;
                break;
            case "50d":
                resource = R.drawable.haze_day;
                break;
            case "50n":
                resource = R.drawable.haze_night;
                break;
            case "01d":
                resource = R.drawable.clear_day;
                break;
            case "01n":
                resource = R.drawable.clear_night;
                break;
            case "02d":
                resource = R.drawable.partly_cloudy;
                break;
            case "02n":
                resource = R.drawable.partly_cloudy_night;
                break;
            case "03d":
                resource = R.drawable.cloudy_weather;
                break;
            case "03n":
                resource = R.drawable.cloudy_weather;
                break;
            case "04d":
                resource = R.drawable.mostly_cloudy;
                break;
            case "04n":
                resource = R.drawable.mostly_cloudy_night;
                break;
            default:
                resource = R.drawable.unknown;
        }
        return resource;
    }
}
