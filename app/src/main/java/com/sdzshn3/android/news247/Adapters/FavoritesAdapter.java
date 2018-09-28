package com.sdzshn3.android.news247.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "news247-favorites";

    public FavoritesAdapter(Context context, ArrayList<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    public News getItem(int position) {
        return mNewsList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final News currentNews = getItem(position);
        final String currentNewsFullUri = currentNews.getApiUrl() + "?api-key=" + BuildConfig.GUARDIAN_API_KEY + "&show-tags=contributor&show-fields=thumbnail";

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_full));
        }
        holder.favoriteButton.setChecked(true);

        holder.favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(mContext, "else", Toast.LENGTH_SHORT).show();
                        holder.favoriteButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_star_border));
                    }
                    editor.remove(currentNewsFullUri);
                    Log.v("111111111111111111", currentNewsFullUri);
                    removeAt(holder.getPosition());
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

        if(showArticleImages) {
            String thumbnailUrl = currentNews.getThumbnail();
            if (thumbnailUrl != null) {
                if (!thumbnailUrl.equals("noImage")) {
                    Picasso.get().load(thumbnailUrl).into(holder.thumbnailView);
                }
            }
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

        if(showAuthorName) {
            if (firstName != null && lastName != null) {
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
            }
        }

        if(currentNews.getPublishedAt() != null) {
            holder.publishedAtView.setText(currentNews.getPublishedAt().replace("T", " at ").replace("Z", " "));
        }
    }

    public void removeAt(int position) {
        mNewsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mNewsList.size());
        notifyDataSetChanged();
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
