package com.example.animemusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.AllPlaylistAdapter;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.interfaces.CommunicationInterface;
import com.example.animemusic.models.ListPlaylistResp;
import com.example.animemusic.models.Playlist;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllForYouFragment extends BaseFragment implements AllPlaylistAdapter.AllPlaylistItemListener {

    private RecyclerView recyclerView;
    private AllPlaylistAdapter adapter;
    private ArrayList<Playlist> data = new ArrayList<>();

    SpinKitView spinKitView;

    private CommunicationInterface communicationInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.communicationInterface = (CommunicationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement onViewSelected!");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_all_playlist;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpView();
    }

    private void setUpView() {
        spinKitView = findViewByID(R.id.mini_loading);
        recyclerView = findViewByID(R.id.playlist_list);
        adapter = new AllPlaylistAdapter(getLayoutInflater());
        recyclerView.setAdapter(adapter);
        getData();
        adapter.setListener(this);
    }

    private void getData() {
        ApiBuilder.getInstance().getFeaturedPlaylists(100, 0).enqueue(new Callback<ListPlaylistResp>() {
            @Override
            public void onResponse(Call<ListPlaylistResp> call, Response<ListPlaylistResp> response) {
                data.clear();
                ListPlaylistResp listPlaylistResp = response.body();
                data.addAll(listPlaylistResp.getPlaylists());
                spinKitView.setVisibility(View.GONE);
                adapter.setPlaylists(data);
            }

            @Override
            public void onFailure(Call<ListPlaylistResp> call, Throwable t) {

            }
        });
    }



    @Override
    public void onPlaylistClickedListener(int position) {
        int id = data.get(position).getId();
        String name = data.get(position).getName();
        String thumbnail = data.get(position).getThumbnailUrl();
        communicationInterface.getAnimeInfo(id, name, thumbnail);
    }
}
