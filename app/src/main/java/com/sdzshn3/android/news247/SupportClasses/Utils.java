package com.sdzshn3.android.news247.SupportClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.sdzshn3.android.news247.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Utils {

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        } else {
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static String setNoOfArticles(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();

        String numberOfArticles = sharedPrefs.getString(
                resources.getString(R.string.number_of_articles_key),
                resources.getString(R.string.default_no_of_news_articles)
        );

        if (numberOfArticles.trim().isEmpty()) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(resources.getString(R.string.number_of_articles_key), "10");
            editor.apply();
            numberOfArticles = sharedPrefs.getString(resources.getString(R.string.number_of_articles_key),
                    resources.getString(R.string.default_no_of_news_articles));
        }
        return numberOfArticles;
    }

    public static void setUpRecyclerView(Context context, RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }
}
