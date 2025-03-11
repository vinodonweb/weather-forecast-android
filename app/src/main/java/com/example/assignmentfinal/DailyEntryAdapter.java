package com.example.assignmentfinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentfinal.databinding.ActivityDailyBinding;
import com.example.assignmentfinal.databinding.ActivityDailyEntryBinding;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyEntryAdapter extends RecyclerView.Adapter<DailyEntryViewHolder> {
    private final List<Daily> dailyList;
    private final Context context;
    private static final String TAG = "DailyEntryAdapter";
    private final String unitGroup;
    private final List<Daily> dayforecasts;  //temp

    public DailyEntryAdapter(Context context, List<Daily> dailyList, String unitGroup, List<Daily> dayforecasts) {
        this.dailyList = dailyList;
        this.context = context;
        this.unitGroup = unitGroup;
        this.dayforecasts = dayforecasts;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public DailyEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityDailyEntryBinding binding = ActivityDailyEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DailyEntryViewHolder(binding.getRoot());
    }



    @Override
    public void onBindViewHolder(@NonNull DailyEntryViewHolder holder, int position){
        Daily daily  = dailyList.get(position);


        if(daily != null){

            double averageTemp = calculateAverageTemperature(dayforecasts);
            ColorMaker.setColorGradient(holder.binding.weatherDetailsCard, averageTemp, unitGroup.equals("metric") ? "C" : "F");

            if (unitGroup.equalsIgnoreCase("metric")) {
                holder.binding.highLow.setText(String.format(Locale.getDefault(), "%.0f°C / %.0f°C", daily.getTempMax(), daily.getTempMin()));
                holder.binding.morningTemp.setText(String.format(Locale.getDefault(), "%.0f°C", daily.getDayMorningTemp()));
                holder.binding.afternoonTemp.setText(String.format(Locale.getDefault(), "%.0f°C", daily.getDayAfternoonTemp()));
                holder.binding.eveningTemp.setText(String.format(Locale.getDefault(), "%.0f°C", daily.getDayEveningTemp()));
                holder.binding.nightTemp.setText(String.format(Locale.getDefault(), "%.0f°C", daily.getDayNightTemp()));
            } else {
                holder.binding.highLow.setText(String.format(Locale.getDefault(), "%.0f°F / %.0f°F", daily.getTempMax(), daily.getTempMin()));
                holder.binding.morningTemp.setText(String.format(Locale.getDefault(), "%.0f°F", daily.getDayMorningTemp()));
                holder.binding.afternoonTemp.setText(String.format(Locale.getDefault(), "%.0f°F", daily.getDayAfternoonTemp()));
                holder.binding.eveningTemp.setText(String.format(Locale.getDefault(), "%.0f°F", daily.getDayEveningTemp()));
                holder.binding.nightTemp.setText(String.format(Locale.getDefault(), "%.0f°F", daily.getDayNightTemp()));
            }

            holder.binding.morningText.setText("Morning");
            holder.binding.afternoonText.setText("Afternoon");
            holder.binding.eveningText.setText("Evening");
            holder.binding.nightText.setText("Night");

            //handle icon loading using picasso
            try {
                String iconResourceName = daily.getIcon().replace("-", "_");
                int iconResourceId = getId(iconResourceName, R.drawable.class);
                if (iconResourceId != 0) {
                    Picasso.get().load(iconResourceId).into(holder.binding.dailyWeatherIcon);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading icon: " + e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return dailyList != null ? dailyList.size() : 0;
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

    private double calculateAverageTemperature(List<Daily> dayforecasts) {
        double sum = 0;
        for (Daily dayForecast : dayforecasts) {
            sum += dayForecast.getTempMax();
        }
        return sum / dayforecasts.size();

    }

}

