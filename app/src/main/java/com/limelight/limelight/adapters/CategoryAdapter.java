package com.limelight.limelight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.limelight.limelight.R;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private ArrayList<String> cat;
    private Context context;
    private LayoutInflater inflter;

    public CategoryAdapter(Context context, ArrayList<String> cat) {
        this.context = context;
        this.cat = cat;

        inflter = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return cat.size();
    }

    @Override
    public Object getItem(int position) {
        return cat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.item_topic, null);
        TextView catName = view.findViewById(R.id.topicName);
        catName.setText(cat.get(position));
        return view;
    }
}
