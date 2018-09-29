package com.sdzshn3.android.news247.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdzshn3.android.news247.R;

public class NewsCategoriesFragment extends Fragment {

    View rootView;
    ViewPager viewPager;
    TabLayout tabLayout;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     rootView = inflater.inflate(R.layout.tab_and_viewpager_layout, container, false);

     viewPager = rootView.findViewById(R.id.viewpager);
     viewPager.setAdapter(new CategoryAdapter(getActivity(), getChildFragmentManager()));
     tabLayout = rootView.findViewById(R.id.sliding_tabs);
     tabLayout.post(new Runnable() {
         @Override
         public void run() {
             tabLayout.setupWithViewPager(viewPager);
         }
     });


     return rootView;
    }

    private class CategoryAdapter extends FragmentPagerAdapter{
        public CategoryAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new NewsFeedTab();
                case 1:
                    return new ScienceNewsTab();
                case 2:
                    return new TechnologyNewsTab();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return mContext.getString(R.string.tab_1_title);
                case 1:
                    return mContext.getString(R.string.tab_2_title);
                case 2:
                    return mContext.getString(R.string.tab_3_title);
                default:
                    return "Tab";
            }
        }
    }

}
