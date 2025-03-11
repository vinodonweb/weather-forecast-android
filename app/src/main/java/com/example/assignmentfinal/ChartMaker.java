package com.example.assignmentfinal;

import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.example.assignmentfinal.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;


public class ChartMaker {




    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static final SimpleDateFormat sdf2 =
            new SimpleDateFormat("yyyy-MM-dd ", Locale.US);

    private static final SimpleDateFormat sdf3 =
            new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.US);

    //private TreeMap<String, Double> temperatureData;
    private final MainActivity mainActivity;
    private final ActivityMainBinding binding;

    public ChartMaker(MainActivity mainActivity, ActivityMainBinding binding) {
        this.mainActivity = mainActivity;
        this.binding = binding;
    }

    public void makeChart(TreeMap<String, Double> temperatureData,
                          long timeMillisIn) {

        setupChart(binding.chart1);
        setupXAxis(binding.chart1);
        setupYAxis(binding.chart1);
        setData(binding.chart1, temperatureData);

//        binding.dateTimeText.setText(sdf3.format(new Date(timeMillisIn)));
        binding.chart1.setVisibility(View.VISIBLE);
    }

    private void setData(LineChart mChart, TreeMap<String, Double> fullResults) {
        ArrayList<Entry> values = new ArrayList<>();

        for (String timeStr : fullResults.keySet()) {
            try {
                // Convert time string to Date object
                String fullDateTime = sdf2.format(new Date()) + timeStr;
                Date dateValue = sdf.parse(fullDateTime);
                long timeAsMs = Objects.requireNonNull(dateValue).getTime();
                float tempForTime = Objects.requireNonNull(fullResults.get(timeStr)).floatValue();
                values.add(new Entry(timeAsMs, tempForTime));
            } catch (Exception e) {
                Log.e("ChartMaker", "Error parsing date: " + e.getMessage());
            }
        }

        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(values, "DataSet 1");
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet); // add the datasets

        LineData data = new LineData(dataSets);
        mChart.setVisibility(View.VISIBLE);
        mChart.clear();
        mChart.setData(data);
        mChart.invalidate();

        // Add vertical line to show current time
        LimitLine llXAxis = new LimitLine(System.currentTimeMillis());// + (30 * 60 * 1000));
        llXAxis.setLineWidth(1f);
        llXAxis.setLineColor(Color.WHITE);
        mChart.getXAxis().removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        mChart.getXAxis().addLimitLine(llXAxis);
    }

    private void setupChart(LineChart mChart) {
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.animateX(500);
        mChart.setExtraBottomOffset(4f);
        mChart.setExtraRightOffset(20f);
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                mainActivity.displayChartTemp(e.getX(), e.getY());
            }

            @Override
            public void onNothingSelected()
            {

            }
        });
    }

    private void setupXAxis(LineChart mChart) {

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setGridColor(Color.parseColor("#DDFFFFFF"));
        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(15);
        xAxis.setLabelRotationAngle(90);
        xAxis.setLabelCount(8, true);
        xAxis.setSpaceMax(0.0f);
        xAxis.setSpaceMin(0.0f);


    }

    private void setupYAxis(LineChart mChart) {

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setValueFormatter(new MyCustomYAxisValueFormatter());
        leftAxis.setGridColor(Color.parseColor("#DDFFFFFF"));

        int orientation = mainActivity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            leftAxis.setLabelCount(4, true);
        } else {
            leftAxis.setLabelCount(6, true);
        }


        leftAxis.setDrawZeroLine(false);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(16);
        leftAxis.setDrawLimitLinesBehindData(true);


    }

    public static class MyCustomXAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat simpleDateFormat;

        MyCustomXAxisValueFormatter() {
            simpleDateFormat = new SimpleDateFormat("h a", Locale.US);
        }

        @Override
        public String getFormattedValue(float value) {
            value += 5 * 60 * 1000;
            Date d = new Date((long) value);
            return simpleDateFormat.format(d).toLowerCase();
        }

    }

    public static class MyCustomYAxisValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.format(Locale.getDefault(), "%.0f°", value);
        }

    }
}
