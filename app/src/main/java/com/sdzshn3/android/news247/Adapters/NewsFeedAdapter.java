package com.sdzshn3.android.news247.Adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sdzshn3.android.news247.Data.FavoriteNewsContract;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.sdzshn3.android.news247.Data.FavoriteNewsContract.FavoriteNewsEntry;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "news247-favorites";
    private static final String showTags = "show-tags";
    private static final String contributorTag = "contributor";
    private static final String showFields = "show-fields";
    private static final String thumbnailField = "thumbnail";


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
        final String currentNewsFullUri = currentNews.getApiUrl() + "&show-tags=contributor&show-fields=thumbnail";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean showAuthorName = sharedPreferences.getBoolean(
                mContext.getString(R.string.author_name_key),
                Boolean.parseBoolean(mContext.getString(R.string.default_show_author_name))
        );

        boolean showArticleImages = sharedPreferences.getBoolean(
                mContext.getString(R.string.show_article_images_key),
                Boolean.parseBoolean(mContext.getString(R.string.default_show_article_images))
        );

        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean(currentNewsFullUri, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.favoriteButton.setChecked(true);
                holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_full));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.favoriteButton.setChecked(false);
                holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_border));
            }
        }

        final ContentValues values = new ContentValues();

        holder.favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    values.put(FavoriteNewsEntry.COLUMN_SECTION_NAME, currentNews.getSectionName());
                    values.put(FavoriteNewsEntry.COLUMN_TITLE, currentNews.getTitle());
                    values.put(FavoriteNewsEntry.COLUMN_ARTICLE_URL, currentNews.getArticleUrl());
                    values.put(FavoriteNewsEntry.COLUMN_API_URI, currentNews.getApiUrl());
                    values.put(FavoriteNewsEntry.COLUMN_PUBLISHED_AT, currentNews.getPublishedAt());
                    values.put(FavoriteNewsEntry.COLUMN_FIRST_NAME, currentNews.getFirstName());
                    values.put(FavoriteNewsEntry.COLUMN_LAST_NAME, currentNews.getLastName());
                    values.put(FavoriteNewsEntry.COLUMN_THUMBNAIL, currentNews.getThumbnail());
                    Uri newUri = mContext.getContentResolver().insert(FavoriteNewsEntry.CONTENT_URI, values);
                    if (newUri == null) {
                        Toast.makeText(mContext, "Error saving this article as favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.favoriteButton.setChecked(true);
                            holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_full));
                        }
                        editor.putBoolean(currentNewsFullUri, true);
                        editor.apply();
                    }

                } else {
                    Uri currentArticleUri = ContentUris.withAppendedId(FavoriteNewsEntry.CONTENT_URI, holder.getAdapterPosition());
                    if (currentArticleUri != null) {
                        Log.v("hahah", currentArticleUri.toString());
                        int rowsDeleted = mContext.getContentResolver().delete(currentArticleUri, null, null);
                        if (rowsDeleted == 0) {
                            Toast.makeText(mContext, "Error removing this article from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.favoriteButton.setChecked(false);
                                holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_border));
                            }
                            editor.remove(currentNewsFullUri);
                            editor.apply();
                        }
                    }
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
            Picasso.get().load(thumbnailUrl).resize(240, 135).into(holder.thumbnailView);
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
