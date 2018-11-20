package com.sdzshn3.android.news247.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;


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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean showArticleImages = sharedPreferences.getBoolean(
                mContext.getString(R.string.show_article_images_key),
                Boolean.parseBoolean(mContext.getString(R.string.default_show_article_images)));

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        holder.shareButton.setBackground(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_share, null));
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getArticleUrl());
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, mContext.getString(R.string.share_article_link_hint)));
            }
        });

        String thumbnailUrl = currentNews.getThumbnail();
        if (thumbnailUrl != null && showArticleImages) {
            if (!thumbnailUrl.isEmpty()) {
                Picasso.get().load(thumbnailUrl).resize(272, 153).into(holder.thumbnailView);
            }
        } else {
            holder.thumbnailView.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.GONE);
        }

        String contributorImageUrl = currentNews.getContributorImage();
        if(contributorImageUrl != null){
            if(!contributorImageUrl.isEmpty()) {
                Picasso.get().load(contributorImageUrl).into(holder.contributorImage);
            }
            else {
                holder.contributorImage.setVisibility(View.GONE);
            }
        }else {
            holder.contributorImage.setVisibility(View.GONE);
        }

        Typeface semiBoldText = Typeface.createFromAsset(mContext.getAssets(), "GoogleSans-Medium.ttf");
        Typeface regularText = Typeface.createFromAsset(mContext.getAssets(), "GoogleSans-Regular.ttf");

        if(currentNews.getSectionName() != null) {
            holder.sectionView.setTypeface(regularText);
            holder.sectionView.setText(currentNews.getSectionName());
        }

        holder.titleView.setTypeface(semiBoldText);
        holder.titleView.setText(currentNews.getTitle());

        String firstName = null;
        String lastName = null;
        try {
            firstName = currentNews.getFirstName();
            lastName = currentNews.getLastName();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (firstName == null && lastName == null) {
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

        if(currentNews.getPublishedAt() != null) {
            if(currentNews.getSectionName() == null) {
                holder.publishedAtView.setText(currentNews.getPublishedAt());
            } else {
                holder.publishedAtView.setText(currentNews.getPublishedAt().replace("T", " at ").replace("Z", " "));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
        @BindView(R.id.share_button)
        Button shareButton;
        @BindView(R.id.contributor_image)
        RoundedImageView contributorImage;
        @BindView(R.id.image_card_view)
        CardView cardView;

        ViewHolder(View listItemView) {
            super(listItemView);
            ButterKnife.bind(this, listItemView);
        }
    }


}
