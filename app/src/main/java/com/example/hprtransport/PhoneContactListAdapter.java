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

public class PhoneContactListAdapter extends ArrayAdapter<Contact> {

    private final Activity context;
    private final List<Contact> contactList;

    public PhoneContactListAdapter(Activity context, List<Contact> contactList) {
        super(context, R.layout.list_item_phone_contact, contactList);
        this.context = context;
        this.contactList = contactList;
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_phone_contact, parent, false);
            holder = new ViewHolder();
            holder.textViewName = convertView.findViewById(R.id.contact_name);
            holder.textViewPhone = convertView.findViewById(R.id.contact_phone_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        if (contact != null) {
            holder.textViewName.setText(contact.name);
            holder.textViewPhone.setText(contact.phoneNumber);
        }

        return convertView;
    }
}
