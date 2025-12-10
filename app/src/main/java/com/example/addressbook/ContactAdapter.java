package com.example.addressbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<HashMap<String, String>> contacts;

    public ContactAdapter(Context context, ArrayList<HashMap<String, String>> contacts, ArrayList<String> names) {
        super(context, android.R.layout.simple_list_item_1, names);
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Add bounds check
        if (position >= 0 && position < contacts.size()) {
            HashMap<String, String> contact = contacts.get(position);
            if (contact != null) {
                holder.textView.setText(contact.get("name"));
            } else {
                holder.textView.setText("Unknown contact");
            }
        } else {
            // Handle the case where position is out of bounds
            holder.textView.setText("Invalid contact");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
