package com.limelight.limelight.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limelight.limelight.R;
import com.limelight.limelight.listeners.RecyclerViewClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KeywordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> strings;
    private RecyclerViewClickListener mListener;

    public KeywordAdapter(ArrayList<String> strings, RecyclerViewClickListener listener) {
        this.strings = strings;
        this.mListener=listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new KeywordAdapter.MyViewHolder(v,mListener); // pass the view to View Holder
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final KeywordAdapter.MyViewHolder viewHolder = (KeywordAdapter.MyViewHolder) holder;
        if(strings.get(position)!=null)
            viewHolder.topicName.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // init the item view's
        TextView topicName;
        private RecyclerViewClickListener mListener;


        MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);

            mListener = listener;
            topicName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }


}
