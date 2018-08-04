package com.sdzshn3.android.news247;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        mContext =context;
        mNewsList = newsList;
    }

    public News getItem(int position){
        return mNewsList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News currentNews = getItem(position);

        String thumbnailUrl = currentNews.getThumbnail();
        Picasso.get().load(thumbnailUrl).into(holder.thumbnailView);

        Typeface semiBoldText = Typeface.createFromAsset(mContext.getAssets(), "Montserrat-SemiBold.ttf");
        Typeface regularText = Typeface.createFromAsset(mContext.getAssets(), "Montserrat-Regular.ttf");

        holder.sectionView.setTypeface(regularText);
        holder.sectionView.setText(currentNews.getSectionName());

        holder.titleView.setTypeface(semiBoldText);
        holder.titleView.setText(currentNews.getTitle());



        String firstName;
        String lastName;
        try {
            firstName = currentNews.getFirstName();
            lastName = currentNews.getLastName();
        }catch (NullPointerException e){
            firstName = "";
            lastName = "";
        }
        if(firstName.equals("") && lastName.equals("")){
            holder.firstNameView.setVisibility(View.GONE);
            holder.lastNameView.setVisibility(View.GONE);
            holder.separator.setVisibility(View.GONE);
        } else {
            if(!firstName.equals("")){
                holder.firstNameView.setText(firstName);
            }
            if(!lastName.equals("")){
                holder.lastNameView.setText(lastName);
            }
            holder.separator.setVisibility(View.VISIBLE);
        }

        holder.publishedAtView.setText(currentNews.getPublishedAt().replace("T", " at ").replace("Z", " "));
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnailView;
        TextView sectionView;
        TextView titleView;
        TextView firstNameView;
        TextView lastNameView;
        TextView separator;
        TextView publishedAtView;

        public ViewHolder(View listItemView) {
            super(listItemView);
            thumbnailView = listItemView.findViewById(R.id.thumbnailImageView);
            sectionView = listItemView.findViewById(R.id.sectionTextView);
            titleView = listItemView.findViewById(R.id.titleTextView);
            firstNameView = listItemView.findViewById(R.id.firstNameTextView);
            lastNameView = listItemView.findViewById(R.id.lastNameTextView);
            separator = listItemView.findViewById(R.id.separator);
            publishedAtView = listItemView.findViewById(R.id.publishedAtTextView);
        }
    }



}
