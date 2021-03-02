package com.example.animemusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.adapter.SongAdapter;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.Color;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.appbar.AppBarLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePlaylistFragment extends Fragment {

    protected AppBarLayout appBarLayout;
    protected SpinKitView miniLoading;
    protected FrameLayout playlistContainer;

    public LinearLayout playlistInfo;
    protected RoundedImageView playlistThumb;
    protected TextView playlistTitle;
    public SongAdapter songAdapter;
    protected ArrayList<Song> songs = new ArrayList<>();
    public RecyclerView recyclerView;


    public abstract void setUpTitle();

    public abstract void getData(SongAdapter songAdapter);

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_baselist, viewGroup, false);
        playlistContainer = inflate.findViewById(R.id.song_container);
        setUpView(inflate);
        setUpPlaylist(inflate);
        setUpTitle();
        getData(songAdapter);
        setUpAppBar();
        return inflate;
    }

    public void onPlaylistLoaded(List<Song> list) {
        Song song;
        this.miniLoading.setVisibility(View.GONE);
        if (list.size() > 0 && (song = list.get(0)) != null) {
            Color.loadBgBannerAverage((int) getResources().getDimension(R.dimen.image_size), playlistContainer, song.getArtworkUrl());
        }
    }

    private void setUpView(View view) {
        playlistInfo = view.findViewById(R.id.info_container);
        playlistThumb = view.findViewById(R.id.playlist_thumb);
        playlistTitle = view.findViewById(R.id.playlist_title);
        miniLoading = view.findViewById(R.id.mini_loading);
        appBarLayout = view.findViewById(R.id.app_bar);
    }

    public void setUpAppBar() {
        AppBarLayout appBarLayout2 = this.appBarLayout;
        if (appBarLayout2 != null) {
            appBarLayout2.setExpanded(true);
        }
    }


    public void setUpPlaylist(View view) {
        recyclerView = view.findViewById(R.id.playlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        songAdapter = new SongAdapter(getLayoutInflater());
        recyclerView.setAdapter(songAdapter);
    }
    public void handleSaveQueueAndPlaySong(ArrayList<Song> list, int i) {
        if (getActivity() != null) {
            if (list.size() == 0) {
                Toast.makeText(getActivity(), getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            QueueManager instance = QueueManager.getInstance();
            if (SharedPrefHelper.getInstance(getActivity()).getShuffleMode(0) == 1) {
                QueueManager.ShufflePlaylist shuffle = instance.shuffle(list, i);
                ArrayList<Song> list2 = (ArrayList<Song>) shuffle.songs;
                i = shuffle.position;
                list = list2;
            }
            if (i >= 0 && i < list.size()) {
                instance.handleSaveQueueAndPlaySong(getActivity(), list, i);
            }
        }
    }


}
