package com.example.animemusic.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.animemusic.R;
import com.example.animemusic.adapter.ProfilePagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends BaseFragment {

    private ProfilePagerAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;

    private TabFavoriteFragment fmTabFavorite = new TabFavoriteFragment();
    private TabPlaylistFragment fmTabPlaylist = new TabPlaylistFragment();

    public TabFavoriteFragment getFmTabFavorite() {
        return fmTabFavorite;
    }

    public TabPlaylistFragment getFmTabPlaylist() {
        return fmTabPlaylist;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_profile;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

    }

    private void initViews() {
        viewPager = findViewByID(R.id.view_pager);
        tab = findViewByID(R.id.tabs);
        tab.setupWithViewPager(viewPager);
        adapter = new ProfilePagerAdapter(getChildFragmentManager(), fmTabPlaylist, fmTabFavorite);
        viewPager.setAdapter(adapter);

    }


}
