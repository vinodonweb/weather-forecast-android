package com.example.assignmentfinal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentfinal.databinding.ActivityHourlyBinding;
import com.example.assignmentfinal.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyViewHolder> {
    private  List<Hourly> hourlyList;
    private  final Context context;
    private static final String TAG = "HourlyAdapter";
    private final String unitGroup;

    public HourlyAdapter(Context context, List<Hourly> hourlyList, String unitGroup) {
        this.context = context;
        this.hourlyList = hourlyList;
        this.unitGroup = unitGroup;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityHourlyBinding binding = ActivityHourlyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HourlyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Hourly hour = hourlyList.get(position);

        if (hour != null) {

            Log.d(TAG, "onBindViewHolder: " + unitGroup);

           if(unitGroup.equalsIgnoreCase("US")){
               holder.binding.temperature.setText(String.format(Locale.getDefault(), "%.0f°", hour.getTemp()));
           } else if(unitGroup.equalsIgnoreCase("Metric")) {
               holder.binding.temperature.setText(String.format(Locale.getDefault(), "%.0f°", hour.getTemp()));
           }

            holder.binding.day.setText(hour.getDay());
            holder.binding.time.setText(hour.getTime());

//            holder.binding.temperature.setText(String.format(Locale.getDefault(), "%.1f°F", hour.getTemp()));
            holder.binding.description.setText(hour.getDescription());

            //handle icon loading using picasso
            try {
                String iconResourceName = hour.getIcon().replace("-", "_");
                int iconResourceId = getId(iconResourceName, R.drawable.class);
                if (iconResourceId != 0) {
                    Picasso.get().load(iconResourceId).into(holder.binding.weatherIcon);
                } else {
                    Picasso.get().load(R.drawable.cloudy).into(holder.binding.weatherIcon);
                }
            } catch (Exception e) {
                holder.binding.weatherIcon.setImageResource(R.drawable.cloudy);
            }
        }
    }
    @Override
    public int getItemCount() {
        return hourlyList != null ? hourlyList.size() : 0;
    }


    private int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateData(List<Hourly> newHourlyList) {
        this.hourlyList = newHourlyList;
        notifyDataSetChanged();
    }
}
