package com.sdzshn3.android.news247.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TeluguNewsAdapter extends ListAdapter<News, TeluguNewsAdapter.ViewHolder> {
    private Context mContext;

    public TeluguNewsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<News> DIFF_CALLBACK = new DiffUtil.ItemCallback<News>() {
        @Override
        public boolean areItemsTheSame(@NonNull News news, @NonNull News t1) {
            return news.getTitle().equals(t1.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull News news, @NonNull News t1) {
            return news.getTitle().equals(t1.getTitle()) &&
                    news.getArticleUrl().equals(t1.getArticleUrl()) &&
                    news.getPublishedAt().equals(t1.getPublishedAt());
        }
    };

    @NonNull
    @Override
    public TeluguNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        mContext = parent.getContext();
        return new TeluguNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TeluguNewsAdapter.ViewHolder holder, int position) {
        final News currentNews = getItem(position);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        //showArticleImages boolean decides whether to show article images or not
        boolean showArticleImages = sharedPreferences.getBoolean(
                mContext.getString(R.string.show_article_images_key),
                Boolean.parseBoolean(mContext.getString(R.string.default_show_article_images)));

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        holder.shareButton.setBackground(VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_share, null));
        //Article URL sharing intent
        holder.shareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, currentNews.getArticleUrl());
            sendIntent.setType("text/plain");
            mContext.startActivity(Intent.createChooser(sendIntent, mContext.getString(R.string.share_article_link_hint)));
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
        holder.contributorImage.setVisibility(View.GONE);

        Typeface semiBoldText = Typeface.createFromAsset(mContext.getAssets(), "GoogleSans-Medium.ttf");


        holder.titleView.setTypeface(semiBoldText);
        holder.titleView.setText(currentNews.getTitle());

        holder.authorNameView.setVisibility(View.GONE);
        holder.separator.setVisibility(View.GONE);

        if(currentNews.getPublishedAt() != null) {
            holder.publishedAtView.setText(currentNews.getPublishedAt().replace("T", " at ").replace("Z", " "));
        }
    }

    @Override
    public News getItem(int position) {
        return super.getItem(position);
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
        @BindView(R.id.authorNameTextView)
        TextView authorNameView;
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
