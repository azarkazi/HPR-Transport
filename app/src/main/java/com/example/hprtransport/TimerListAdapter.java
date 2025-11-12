package com.example.hprtransport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TimerListAdapter extends ArrayAdapter<TimerItem> {

    private final Activity context;
    private final List<TimerItem> timerList;
    private final OnUpdateButtonClickListener listener;

    public interface OnUpdateButtonClickListener {
        void onUpdateButtonClick(TimerItem timerItem);
    }

    public TimerListAdapter(Activity context, List<TimerItem> timerList, OnUpdateButtonClickListener listener) {
        super(context, R.layout.list_item_timer, timerList);
        this.context = context;
        this.timerList = timerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_timer, parent, false);

        TextView timerLabel = rowView.findViewById(R.id.timer_label);
        TextView timerValue = rowView.findViewById(R.id.timer_value);
        Button updateButton = rowView.findViewById(R.id.btn_update_timer);

        TimerItem currentTimer = timerList.get(position);

        timerLabel.setText(currentTimer.getLabel());
        timerValue.setText(currentTimer.getFormattedValue());

        updateButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateButtonClick(currentTimer);
            }
        });

        return rowView;
    }
}
