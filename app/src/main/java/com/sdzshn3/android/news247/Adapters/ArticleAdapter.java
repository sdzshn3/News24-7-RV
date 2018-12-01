package com.sdzshn3.android.news247.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;


public class ArticleAdapter extends ListAdapter<Article, ArticleAdapter.ViewHolder> {

    private Context mContext;

    public ArticleAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Article> DIFF_CALLBACK = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article news, @NonNull Article t1) {
            return news.getTitle().equals(t1.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article news, @NonNull Article t1) {
            return news.getTitle().equals(t1.getTitle()) &&
                    news.getSource().getName().equals(t1.getSource().getName()) &&
                    news.getUrl().equals(t1.getUrl()) &&
                    news.getPublishedAt().equals(t1.getPublishedAt());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Article currentArticle = getItem(position);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        //showAuthorName boolean decides whether to show author name images or not
        boolean showAuthorName = sharedPreferences.getBoolean(
                mContext.getString(R.string.author_name_key),
                Boolean.parseBoolean(mContext.getString(R.string.default_show_author_name))
        );
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, currentArticle.getUrl());
            sendIntent.setType("text/plain");
            mContext.startActivity(Intent.createChooser(sendIntent, mContext.getString(R.string.share_article_link_hint)));
        });

        String thumbnailUrl = currentArticle.getUrlToImage();
        if (currentArticle.getUrlToImage() != null && !currentArticle.getUrlToImage().isEmpty() && showArticleImages) {
            Picasso.get().load(thumbnailUrl).resize(320, 180).into(holder.thumbnailView);
        } else {
            holder.thumbnailView.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.GONE);
        }

        Typeface semiBoldText = Typeface.createFromAsset(mContext.getAssets(), "GoogleSans-Medium.ttf");
        Typeface regularText = Typeface.createFromAsset(mContext.getAssets(), "GoogleSans-Regular.ttf");

        if (currentArticle.getSource().getName() != null) {
            holder.sectionView.setTypeface(regularText);
            holder.sectionView.setText(currentArticle.getSource().getName().replace(".com", "").replace(".in", "").replace(".co", "")
                    .replace("www.", "").replace("Www.", "").replace(".co.in", "").replace(".org", ""));
        }

        holder.titleView.setTypeface(semiBoldText);
        holder.titleView.setText(currentArticle.getTitle());

        if (currentArticle.getAuthor() != null && !currentArticle.getAuthor().isEmpty() && showAuthorName) {
            holder.authorNameView.setVisibility(View.VISIBLE);
            holder.separator.setVisibility(View.VISIBLE);
            holder.authorNameView.setText(currentArticle.getAuthor());
        } else {
            holder.authorNameView.setVisibility(View.GONE);
            holder.separator.setVisibility(View.GONE);
        }


        if (currentArticle.getPublishedAt() != null) {
            holder.publishedAtView.setText(currentArticle.getPublishedAt().replace("T", " at ").replace("Z", " "));
        }
    }

    @Override
    public Article getItem(int position) {
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
        @BindView(R.id.image_card_view)
        CardView cardView;

        ViewHolder(View listItemView) {
            super(listItemView);
            ButterKnife.bind(this, listItemView);
        }
    }


}
