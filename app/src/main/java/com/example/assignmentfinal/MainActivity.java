    package com.example.assignmentfinal;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Color;
    import android.location.Address;
    import android.location.Geocoder;
    import android.net.Uri;
    import android.os.Bundle;
    import android.text.InputType;
    import android.util.Log;
    import android.view.Gravity;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.TextView;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.assignmentfinal.databinding.ActivityMainBinding;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;
    import com.squareup.picasso.Picasso;
    import com.example.assignmentfinal.visualCrossing;

    import java.io.IOException;
    import java.lang.reflect.Field;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.TreeMap;
    import android.Manifest;
    import android.widget.Toast;

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";
        private ActivityMainBinding binding;
        public String location;
        public String unitGroup = "metric";
        private HourlyAdapter hourlyAdapter;
        private List<Hourly> hourlyList = new ArrayList<>();
        private ChartMaker chartMaker;
        private Context context;
        private RecyclerView recyclerView;
        private List<Daily> dailyList = new ArrayList<>();
        private MainActivity MainActivity;
        private CurrentConditions currentConditions;
        private List<Daily> days;
        private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        private FusedLocationProviderClient mFusedLocationClient;
        private static final int LOCATION_REQUEST = 111;
        private static String locationString = "Unspecified Location";



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            determineLocation();


            binding.progressBar.setVisibility(View.VISIBLE);
            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            hourlyAdapter = new HourlyAdapter(this, this.hourlyList, unitGroup);
            recyclerView.setAdapter(hourlyAdapter);


            visualCrossing.makeApiRequest(location, unitGroup, this);

            binding.swipe.setOnRefreshListener(() -> {
                visualCrossing.makeApiRequest(location, unitGroup, this);
                binding.swipe.setRefreshing(false);
            });

        }

        private void determineLocation() {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                // Use default location if permission not granted
                visualCrossing.makeApiRequest(location, unitGroup, this);
            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            1
                                    );
                                    if (addresses != null && !addresses.isEmpty()) {
                                        this.location = addresses.get(0).getLocality();
                                        if (this.location == null || this.location.isEmpty()) {
                                            this.location = "chicago"; // Fallback if geocoding fails
                                        }
                                    } else {
                                        this.location = "chicago"; // Fallback if no addresses
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    this.location = "chicago"; // Fallback on error
                                }
                            } else {
                                this.location = "chicago"; // Fallback if location is null
                            }
                            visualCrossing.makeApiRequest(this.location, unitGroup, this);
                        })
                        .addOnFailureListener(this, e -> {
                            Log.d(TAG, "determineLocation: Failed to get location");
                            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                            this.location = "chicago"; // Fallback on failure
                            visualCrossing.makeApiRequest(this.location, unitGroup, this);
                        });
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == LOCATION_REQUEST) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                    Toast.makeText(this, "Using default location", Toast.LENGTH_SHORT).show();
                    // Use default location if permission denied
                    visualCrossing.makeApiRequest(location, unitGroup, this);
                }
            }
        }

        public void shareToMap(View view){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + location));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app found to handle this", Toast.LENGTH_SHORT).show();
            }
        }

        public void resetCurrentLocation(View view) {
            determineLocation();
        }

        public void share(View view) {
            if (currentConditions == null || days == null || days.isEmpty()) {
                Toast.makeText(this, "Weather data is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            String city = currentConditions.cityName;
            String unit = unitGroup.equals("metric") ? "°C" : "°F";
            String windSpeedUnit = unitGroup.equals("metric") ? "km/h" : "mph";
            String visibilityUnit = unitGroup.equals("metric") ? "km" : "mi";

            String subject = String.format("Weather for %s)", city);
            @SuppressLint("DefaultLocale") String text = String.format(
                    "Weather for %s:\n\n" +
                            "Forecast: %s with a high of %.1f%s and a low of %.1f%s.\n\n" +
                            "Now: %.1f%s, %s (Feels like: %.1f%s)\n\n" +
                            "Humidity: %.1f%%\n" +
                            "Winds: %s at %.1f %s\n" +
                            "UV Index: %.1f\n" +
                            "Sunrise: %s\n" +
                            "Sunset: %s\n" +
                            "Visibility: %.1f %s",
                    city,
                    currentConditions.conditions, days.get(0).tempMax, unit, days.get(0).tempMin, unit,
                    currentConditions.temp, unit, currentConditions.conditions, currentConditions.feelsLike, unit,
                    currentConditions.humidity,
                    getDirection(currentConditions.windDir), currentConditions.windSpeed, windSpeedUnit,
                    currentConditions.uvIndex,
                    formatEpochToTime(currentConditions.sunriseEpoch),
                    formatEpochToTime(currentConditions.sunsetEpoch),
                    currentConditions.visibility, visibilityUnit
            );

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }


        public void toggleUnitGroup(View v) {
            if (unitGroup.equalsIgnoreCase("metric")) {
                unitGroup = "us";
                binding.fera.setImageResource(R.drawable.units_f);
                visualCrossing.makeApiRequest(location, unitGroup, this);
                hourlyAdapter.notifyDataSetChanged();
            } else {
                unitGroup = "metric";
                binding.fera.setImageResource(R.drawable.units_c);
                visualCrossing.makeApiRequest(location, unitGroup, this);
                hourlyAdapter.notifyDataSetChanged();
            }
        }

        private TreeMap<String, Double> makeTemperaturePoints(List<Hourly> hourforecasts) {
            TreeMap<String, Double> timeTempValues = new TreeMap<>();

            // Iterate over hourly conditions to populate time and temperature values
            for (Hourly hourforecast : hourforecasts) {
                // Get the time for that hour
                String time = hourforecast.getDatetime();

                // Get the temperature for that hour
                double temperature = hourforecast.getTemp();

                // Put the time and temperature in the map
                timeTempValues.put(time, temperature);
            }
            Log.d(TAG, "Temperature Points: " + timeTempValues); // Add logging to check values
            return timeTempValues;
        }

        public void openDailyActivity(View view) {
            String locationToSend = location != null ? location : "chicago";
            Intent intent = new Intent(this, DailyActivity.class);
            intent.putExtra("location", locationToSend);
            intent.putExtra("unitGroup", unitGroup);
            startActivity(intent);
        }


        public void handlesearch(View v){
            final EditText locationInput = new EditText(this);
            locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
            locationInput.setGravity(Gravity.CENTER_HORIZONTAL);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter a Location");
            builder.setMessage("For US Locations, enter as 'City', or 'City, State' \nFor International Locations, enter as 'City, Country'");
            builder.setView(locationInput);
            builder.setNegativeButton("Cancel", (dialog, id) -> Log.d(TAG, "handleSearch: Cancelled search"));
            builder.setPositiveButton("OK", (dialog, id) -> {
                location = locationInput.getText().toString();
                visualCrossing.makeApiRequest(location, unitGroup, this);

                Log.d(TAG, "handleSearch: Location entered: " + location);
                });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void updateUI(double temp, String description, double feelsLike, int cloudCover,
                             String windDirection, double windSpeed, double visibility, Long sunsetEpoch, Long sunriseEpoch,
                             int humidity, int uvIndex, String resolvedAddress, String icon, String currentDateFormated, String cityName) {
            runOnUiThread(() ->{

            binding.currentTemp.setText(String.format(Locale.getDefault(), "%.1f°F", temp));
            binding.currentFeelsLike.setText(String.format(Locale.getDefault(), "Feels like: %.1f°F", feelsLike));
            binding.currentDescription.setText(String.format(Locale.getDefault(), "%s (%d%% clouds)", description,cloudCover));
            binding.currentWinddir.setText(String.format(Locale.getDefault(), "Wind: %s at %.1f mph", windDirection, windSpeed));
            binding.currentVisability.setText(String.format(Locale.getDefault(), "Visibility: %.1f mi", visibility));
            binding.currentSunrise.setText(String.format(Locale.getDefault(), "Sunrise: %s", new SimpleDateFormat("h:mm a", Locale.getDefault()).format(sunriseEpoch * 1000)));
            binding.currentSunset.setText(String.format(Locale.getDefault(), "Sunset: %s", new SimpleDateFormat("h:mm a", Locale.getDefault()).format(sunsetEpoch * 1000)));
            binding.currentHumidity.setText(String.format(Locale.getDefault(), "Humidity: %d%%", humidity));
            binding.currentUvIndex.setText(String.format(Locale.getDefault(), "UV Index: %d", uvIndex));
            binding.mainHeading.setText(String.format("%s, %s", cityName, currentDateFormated));

            //set the weather icon based on Stirng
            try {
                // Convert icon string to resource name format (replace hyphens with underscores)
                String iconResourceName = icon.replace("-", "_");
                // Get the resource ID
                int iconResourceId = getId(iconResourceName, R.drawable.class);
                if (iconResourceId != 0) {
                    Picasso.get().load(iconResourceId).into(binding.currentIcon);
                } else {
                    // Fallback to default icon if the resource is not found
                    Picasso.get().load(R.drawable.cloudy).into(binding.currentIcon);
                    Log.w(TAG, "Weather icon resource not found: " + icon);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting weather icon", e);
                binding.currentIcon.setImageResource(R.drawable.cloudy);
            }
            });
        }

        // Helper method to get resource ID from string
        private static int getId(String resourceName, Class<?> c) {
            try {
                Field idField = c.getDeclaredField(resourceName);
                return idField.getInt(idField);
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            binding = null;  // Clean up binding reference
        }

        public void displayChartTemp(float time, float tempVal) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("h a", Locale.US);
            Date d = new Date((long) time);
            binding.chartTemp.setText(
                    String.format(Locale.getDefault(),
                            "%s, %.0f°",
                            sdf.format(d), tempVal));
            binding.chartTemp.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    runOnUiThread(() -> binding.chartTemp.setVisibility(View.GONE));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public void showLocationNotFoundError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Error");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("The specified location \"" + location + "\" could not be resolved. please try a different location.");
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void showServerError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("this app requires an internet connection to function properly. Please check your connection and try again.");
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void showerror() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Weather Data Error");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("There was an error retrieving the weather data. Please try again later.");
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void updateData(CurrentConditions currentConditions, List<Daily> dailyForecasts) {

            binding.progressBar.setVisibility(View.VISIBLE);
            this.currentConditions = currentConditions; // Store the current conditions
            if (currentConditions != null) {

                //change the color:
                ColorMaker.setColorGradient(binding.main, currentConditions.temp, unitGroup.equals("metric") ? "C" : "F");

                binding.progressBar.setVisibility(View.GONE);

    //            // Set the color of the icon bar (assuming it's in a layout called 'iconBar')
                int[] colors = ColorMaker.getTemperatureColor((int) currentConditions.temp);
                String startColorString = String.format(Locale.getDefault(), "#FF%02x%02x%02x", colors[0], colors[1], colors[2]);
                String endColorString = String.format(Locale.getDefault(), "#99%02x%02x%02x", colors[0], colors[1], colors[2]);
                binding.iconbar.setBackgroundColor(Color.parseColor(startColorString)); // Replace 'iconBar' with the actual ID of your icon bar
    //
                binding.recyclerView.setBackgroundColor(Color.parseColor(endColorString)); // Set the RecyclerView's background

                // Get the current date and time
                Date now = new Date();

                // Format the date and time
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm a", Locale.getDefault());
                String currentDateAndTime = sdf.format(now);
                binding.mainHeading.setText(currentConditions.cityName + ", " + currentDateAndTime);

                if (unitGroup.equals("metric")) {
                    binding.currentTemp.setText(String.format(Locale.getDefault(), "%.0f°C", currentConditions.temp));
                    binding.currentFeelsLike.setText(String.format(Locale.getDefault(), "Feels Like %.0f°C", currentConditions.feelsLike));
                } else {
                    binding.currentTemp.setText(String.format(Locale.getDefault(), "%.0f°F", currentConditions.temp));
                    binding.currentFeelsLike.setText(String.format(Locale.getDefault(), "Feels Like %.0f°F", currentConditions.feelsLike));
                }

                binding.currentDescription.setText(String.format("%s (%.0f%% clouds)", currentConditions.conditions, currentConditions.cloudcover));

                // Get the wind direction as a string
                String windDirection = getDirection(currentConditions.windDir);

                if (unitGroup.equals("metric")) {
                    binding.currentWinddir.setText(String.format(Locale.getDefault(), "Winds: %s at %.1f km/h gusting to %.1f km/h",
                            windDirection, currentConditions.windSpeed, currentConditions.windGust));
                } else {
                    binding.currentWinddir.setText(String.format(Locale.getDefault(), "Winds: %s at %.1f mph gusting to %.1f mph",
                            windDirection, currentConditions.windSpeed, currentConditions.windGust));
                }


                binding.currentHumidity.setText(String.format("Humidity: %.0f%%", currentConditions.humidity));
                binding.currentUvIndex.setText(String.format("UV Index: %.0f", currentConditions.uvIndex));
                if (unitGroup.equals("metric")) {
                    binding.currentVisability.setText(String.format(Locale.getDefault(), "Visibility: %.1f km", currentConditions.visibility));
                } else {
                    binding.currentVisability.setText(String.format(Locale.getDefault(), "Visibility: %.1f mi", currentConditions.visibility));
                }
                binding.currentSunrise.setText(String.format("Sunrise: %s", formatEpochToTime(currentConditions.sunriseEpoch)));
                binding.currentSunset.setText(String.format("Sunset: %s", formatEpochToTime(currentConditions.sunsetEpoch)));

//                binding.currentSunrise.setText(String.format(Locale.getDefault(), "Sunrise: %s", formatEpochToTime(currentConditions.sunriseEpoch)));
//                binding.currentSunset.setText(String.format(Locale.getDefault(), "Sunset: %s", formatEpochToTime(currentConditions.sunsetEpoch)));

                // Set the appropriate weather icon (You might need a method to map `currentConditions.icon` to drawable resources)
                int iconResId = getIconResource(currentConditions.icon);
                if (iconResId != 0) {
                    binding.currentIcon.setImageResource(iconResId);
                }
            }
            if (dailyForecasts != null) {
                this.days = dailyForecasts; // Store the daily forecasts
            }

        }

        private int getIconResource(String icon) {
            switch (icon) {
                case "clear-day":
                    return R.drawable.clear_day;
                case "clear-night":
                    return R.drawable.clear_night;
                case "cloudy":
                    return R.drawable.cloudy;
                case "fog":
                    return R.drawable.fog;
                case "partly-cloudy-day":
                    return R.drawable.partly_cloudy_day;
                case "partly-cloudy-night":
                    return R.drawable.partly_cloudy_night;
                case "rain":
                    return R.drawable.rain;
                case "sleet":
                    return R.drawable.sleet;
                case "snow":
                    return R.drawable.snow;
                case "wind":
                    return R.drawable.wind;
                default:
                    return 0;
            }
        }

        // The existing getDirection method remains unchanged
        private String getDirection(double degrees) {
            if (degrees >= 337.5 || degrees < 22.5)
                return "N";
            if (degrees >= 22.5 && degrees < 67.5)
                return "NE";
            if (degrees >= 67.5 && degrees < 112.5)
                return "E";
            if (degrees >= 112.5 && degrees < 157.5)
                return "SE";
            if (degrees >= 157.5 && degrees < 202.5)
                return "S";
            if (degrees >= 202.5 && degrees < 247.5)
                return "SW";
            if (degrees >= 247.5 && degrees < 292.5)
                return "W";
            if (degrees >= 292.5 && degrees < 337.5)
                return "NW";
            return "X"; // Default for bad values
        }

        // Helper method to format the epoch time to human-readable time
        private String formatEpochToTime(long epochSeconds) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return timeFormat.format(new Date(epochSeconds * 1000)); // Convert seconds to milliseconds
        }

        public void updateHourForecasts(List<Hourly> hourForecasts) {
            Log.d(TAG, "updateHourForecasts called with data: " + hourForecasts.size()); // Add log to check
            this.hourlyList.clear(); // Clear old data if necessary
            this.hourlyList.addAll(hourForecasts); // Add new data
            hourlyAdapter.notifyDataSetChanged(); // Notify adapter of data change

            // Create temperature points for the chart using the hourly data
            TreeMap<String, Double> tempPoints = makeTemperaturePoints(hourlyList);
            Log.d(TAG, "Temperature points generated: " + tempPoints); // Add logging for tempPoints

            ChartMaker chartMaker = new ChartMaker(this, binding); // Ensure correct context and binding are passed
            chartMaker.makeChart(tempPoints, System.currentTimeMillis());
        }
    }