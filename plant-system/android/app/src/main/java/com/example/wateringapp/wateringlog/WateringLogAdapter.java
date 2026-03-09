package com.example.wateringapp.wateringlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wateringapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WateringLogAdapter extends RecyclerView.Adapter<WateringLogAdapter.LogViewHolder> {

    private List<WateringLog> logList = List.of(); // default empty list

    public void setLogs(List<WateringLog> logs) {
        this.logList = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watering_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        WateringLog log = logList.get(position);

        String formattedTime = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                .format(new Date(log.timestamp));

        holder.timestampText.setText(formattedTime);
        holder.amountText.setText("Amount: " + log.amount + "ml");
        holder.modeText.setText("Mode: " + log.mode);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText, amountText, modeText;

        LogViewHolder(View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.text_timestamp);
            amountText = itemView.findViewById(R.id.text_amount);
            modeText = itemView.findViewById(R.id.text_mode);
        }
    }
}
