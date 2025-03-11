# Visual Crossing Weather App 🌤️  

## Overview  
This is an Android weather application that retrieves and displays weather data using the **Visual Crossing Weather API**. Users can view the **current weather**, **hourly forecast**, and **15-day forecast** for their location or a selected city.  

## Features  
✅ **Current Weather & Forecasts**  
- Displays **current weather conditions** (temperature, humidity, UV index, wind speed, etc.).  
- **Hourly forecast** shown in a horizontal RecyclerView.  
- **15-day forecast** available in a dedicated screen.  

✅ **Location Features**  
- Uses **Fused Location API** to detect the user's current location.  
- Allows users to **search for and select a city** to view its weather.  
- Displays the **selected location on a map**.  
- Users can **reset to their current location**.  

✅ **User Experience & UI**  
- **Dynamic gradient backgrounds** based on temperature.  
- **RecyclerView integration** for hourly and daily forecasts.  
- **Weather icons** dynamically fetched and displayed.  
- **Graphing library** used to display temperature trends.  

✅ **Additional Functionalities**  
- **Toggle between Fahrenheit (°F) and Celsius (°C).**  
- **Share weather details** via other apps.  
- **Connectivity check** to handle network failures.  

## Tech Stack 🛠️  
- **Android Studio** (Java/Kotlin)  
- **Visual Crossing Weather API**  
- **Android Volley** for network requests  
- **Fused Location API** for GPS-based location  
- **Picasso/Glide** for image loading  
- **RecyclerView & View Binding** for UI efficiency  
- **Implicit Intents** for sharing & maps  

## How to Run the App 🚀  
1. Clone the repository:  
   ```sh  
   git clone git@github.com:vinodonweb/weather-forecast-android.git  
   cd weather-app  
   ```  
2. Open the project in **Android Studio**.  
3. Obtain a free **API Key** from [Visual Crossing Weather](https://www.visualcrossing.com/) and add it to `strings.xml`:  
   ```xml  
   <string name="weather_api_key">YOUR_API_KEY_HERE</string>  
   ```  
4. Run the app on an emulator or physical device.  

## Screenshots 📸  
![Screenshot 2025-03-11 112032](https://github.com/user-attachments/assets/da1f8d65-e3f5-42f3-83b2-3bbd2b57b4d4)
![Screenshot 2025-03-11 112055](https://github.com/user-attachments/assets/d2c7bf1e-48a8-421e-b051-ebdb0853e092)

  ### 15-Day Forecast
  
![Screenshot 2025-03-11 112119](https://github.com/user-attachments/assets/fc89f4a8-951b-406b-bec5-b3e6afbf04d9)
![Screenshot 2025-03-11 112107](https://github.com/user-attachments/assets/2eb48ef3-6d3e-4dda-996f-baadb58c4d66)

   ### Temperature-Based Color Changing Backgrounds

![Screenshot 2025-03-11 112843](https://github.com/user-attachments/assets/fd4b085b-b56f-4b6b-a449-117a0c763ea8)


## License 📜  
This project is for educational purposes only. 
