package com.limelight.limelight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limelight.limelight.R;
import com.limelight.limelight.listeners.RecyclerViewClickListener;
import com.limelight.limelight.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Article> feedArticles;
    private Context context;
    private RecyclerViewClickListener mListener;

    public FeedAdapter(ArrayList<Article> feedArticles, RecyclerViewClickListener listener) {
        this.mListener = listener;
        this.feedArticles = feedArticles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new MyViewHolder(v, mListener); // pass the view to View Holder

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MyViewHolder viewHolder = (MyViewHolder) holder;
        if (feedArticles.get(position).getTitle() != null)
            viewHolder.articleTitle.setText(feedArticles.get(position).getTitle());
        if (feedArticles.get(position).getSource() != null)
            viewHolder.articleSource.setText(feedArticles.get(position).getSource());
        if (feedArticles.get(position).getImage() != null)
            Picasso.get().load(feedArticles.get(position).getImage()).placeholder(R.drawable.app_logo).into(viewHolder.articleThumb);

        //click to read article

    }

    @Override
    public int getItemCount() {
        return feedArticles.size();
    }

    public void clear() {
        feedArticles.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Article> list) {
        feedArticles.addAll(list);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // init the item view's
        TextView articleTitle, articleSource;
        ImageView articleThumb;
        RelativeLayout article;
        private RecyclerViewClickListener mListener;


        MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            article = itemView.findViewById(R.id.article);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSource = itemView.findViewById(R.id.articleSource);
            articleThumb = itemView.findViewById(R.id.articleThumb);
            mListener = listener;
            article.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }


}
