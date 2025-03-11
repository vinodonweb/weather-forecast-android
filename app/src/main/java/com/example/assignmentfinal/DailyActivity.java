package com.example.assignmentfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentfinal.databinding.ActivityDailyBinding;


import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {

    private static final String TAG = "DailyActivity";

    public ActivityDailyBinding binding;
    public DailyEntryAdapter dailyEntryAdapter;
    public List<Daily> dailyEntryList = new ArrayList<>();
    public String location;
    public String unitGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent.hasExtra("location")) {
            location = intent.getStringExtra("location");
            if(location == null){
                location = "chicago";
            }
            Log.d(TAG, "onCreate: location: " + location);
        }
        if (intent.hasExtra("unitGroup")) {
            unitGroup = intent.getStringExtra("unitGroup");
            Log.d(TAG, "onCreate: unitGroup: " + unitGroup);
        }

        RecyclerView recyclerView = binding.dailyRecycerView;
        dailyEntryAdapter = new DailyEntryAdapter(this, dailyEntryList, unitGroup, dailyEntryList); //temp change
        recyclerView.setAdapter(dailyEntryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        binding.dailyTitle.setText(String.format("%s 15-Days Forecast", location));

        visualCrossing.doForecast(location, unitGroup, this);

    }

    public void addDayForecast(List<Daily> dayforecasts) {
        if (dayforecasts == null) {
            Log.e(TAG, "addDayForecast: dayforecasts is null");
            return;
        } else {

            this.dailyEntryList.clear();
            this.dailyEntryList.addAll(dayforecasts);
            dailyEntryAdapter.notifyDataSetChanged();

            // Set the background color based on the average temperature of the first forecast day
            if (!dayforecasts.isEmpty()) {
                double averageTemp = calculateAverageTemperature(dayforecasts);
                String unit = getIntent().getStringExtra("unitGroup");
                ColorMaker.setColorGradient(binding.getRoot(), averageTemp, unit.equals("metric") ? "C" : "F");

            }
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