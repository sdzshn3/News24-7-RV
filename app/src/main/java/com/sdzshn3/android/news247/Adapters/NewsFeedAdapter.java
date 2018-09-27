package com.sdzshn3.android.news247.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "news247-favorites";


    public NewsFeedAdapter(Context context, ArrayList<News> newsList) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final News currentNews = getItem(position);

        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean(currentNews.getApiUrl(), false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_full));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_border));
            }
        }

        holder.favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_full));
                    }

                    editor.putBoolean(currentNews.getApiUrl(), true);
                    editor.apply();

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_border));
                    }

                    editor.remove(currentNews.getApiUrl());
                    editor.apply();
                }
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getArticleUrl());
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, "Share link"));
            }
        });

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
        @BindView(R.id.favorite_button)
        ToggleButton favoriteButton;
        @BindView(R.id.share_button)
        Button shareButton;

        ViewHolder(View listItemView) {
            super(listItemView);
            ButterKnife.bind(this, listItemView);
        }
    }


}
