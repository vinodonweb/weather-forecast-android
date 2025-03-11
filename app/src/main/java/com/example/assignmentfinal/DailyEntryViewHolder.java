package com.example.assignmentfinal;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignmentfinal.databinding.ActivityDailyEntryBinding;

public class DailyEntryViewHolder extends RecyclerView.ViewHolder{

    public final ActivityDailyEntryBinding binding;

    public DailyEntryViewHolder(View view) {
        super(view);
        binding = ActivityDailyEntryBinding.bind(view);
    }

}
