package com.sdzshn3.android.news247;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    public News getItem(int position) {
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
        if (!thumbnailUrl.equals("noImage")) {
            Picasso.get().load(thumbnailUrl).into(holder.thumbnailView);
        }

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
        } catch (NullPointerException e) {
            firstName = "";
            lastName = "";
        }
        if (firstName.equals("") && lastName.equals("")) {
            holder.firstNameView.setVisibility(View.GONE);
            holder.lastNameView.setVisibility(View.GONE);
            holder.separator.setVisibility(View.GONE);
        } else {
            if (!firstName.equals("")) {
                holder.firstNameView.setText(firstName);
            }
            if (!lastName.equals("")) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnailImageView)
        ImageView thumbnailView;
        @BindView(R.id.sectionTextView)
        TextView sectionView;
        @BindView(R.id.titleTextView)
        TextView titleView;
        @BindView(R.id.firstNameTextView)
        TextView firstNameView;
        @BindView(R.id.lastNameTextView)
        TextView lastNameView;
        @BindView(R.id.separator)
        TextView separator;
        @BindView(R.id.publishedAtTextView)
        TextView publishedAtView;

        ViewHolder(View listItemView) {
            super(listItemView);
            ButterKnife.bind(this, listItemView);
        }
    }


}
