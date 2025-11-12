package com.example.hprtransport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private final Activity context;
    private final List<User> userList;

    public UserListAdapter(Activity context, List<User> userList) {
        super(context, R.layout.list_item_user, userList);
        this.context = context;
        this.userList = userList;
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
        TextView textViewVehicle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_user, parent, false);
            holder = new ViewHolder();
            holder.textViewName = convertView.findViewById(R.id.user_name);
            holder.textViewPhone = convertView.findViewById(R.id.user_phone);
            holder.textViewVehicle = convertView.findViewById(R.id.user_vehicle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        if (user != null) {
            holder.textViewName.setText(user.name);
            holder.textViewPhone.setText(user.phoneNumber);
            holder.textViewVehicle.setText(user.vehicleNumber);
        }

        return convertView;
    }
}
