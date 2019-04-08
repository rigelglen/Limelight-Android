package com.limelight.limelight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.limelight.limelight.R;
import com.limelight.limelight.models.Topic;

import java.util.ArrayList;

public class TopicAdapter extends ArrayAdapter<Topic> implements View.OnClickListener {
    private ArrayList<Topic> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
    }

    public TopicAdapter(ArrayList<Topic> data, Context context) {
        super(context, R.layout.item_topic, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public void onClick(View v) {
        //load activity getfeed by topic
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Topic p = getItem(position);
//        ViewHolder viewHolder; // view lookup cache stored in tag
//
//        final View result;
//
//        if (convertView == null){
//            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.item_topic, parent, false);
//            viewHolder.txtName=convertView.findViewById(R.id.topicName);
//            result=convertView;
//
//            convertView.setTag(viewHolder);
//        }else {
//            viewHolder = (ViewHolder) convertView.getTag();
//            result=convertView;
//        }
//
//        return convertView;

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.item_topic, null);
        }


        if (p != null) {
            TextView topicName = v.findViewById(R.id.topicName);


            if (topicName != null) {
                topicName.setText(p.getName());
            }


        }

        return v;
    }
}
