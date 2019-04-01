package com.limelight.limelight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limelight.limelight.R;
import com.limelight.limelight.models.Article;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Article> feedArticles;
    private Context context;

    public FeedAdapter(ArrayList<Article> feedArticles, Context context)
    {
        this.context = context;
        this.feedArticles = feedArticles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new MyViewHolder(v); // pass the view to View Holder

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.articleTitle.setText(feedArticles.get(position).getTitle());


        //click to read article
    }

    @Override
    public int getItemCount() {
        return feedArticles.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView articleTitle;


        public MyViewHolder(View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.articleTitle);

        }
    }






}
