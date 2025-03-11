package com.example.assignmentfinal;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignmentfinal.databinding.ActivityHourlyBinding;

public class HourlyViewHolder extends  RecyclerView.ViewHolder {

    public final ActivityHourlyBinding binding;

    public HourlyViewHolder(View view) {
        super(view);
        binding = ActivityHourlyBinding.bind(view);
    }
}
