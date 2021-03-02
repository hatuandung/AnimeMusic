package com.example.animemusic.fragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.SongAdapter;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.Helper;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.example.animemusic.view.LocalSongOptionsDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.animemusic.utils.Constant.FAVORITE_PLAYLIST_ID;

public class TabFavoriteFragment extends BaseFragment implements SongAdapter.SongItemListener {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private ArrayList<Song> data = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_profile_tab_favorite;
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

    public void initViews() {
        recyclerView = findViewByID(R.id.rv_favorite);
        adapter = new SongAdapter(getLayoutInflater());
        recyclerView.setAdapter(adapter);
        adapter.setListener(this);
        getData();
    }

    public void getData() {
        data.clear();
        data.addAll(AppDatabase.getInstance(getActivity()).getSongDao().getByPlaylistId(1, 99999, 0));
        if (adapter != null) {
            adapter.setSongs(data);
        }

    }

    @Override
    public void onSongClickedListener(int position) {
        handleSaveQueueAndPlaySong(data, position);
        if (getActivity() != null && (getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    @Override
    public void onSongLongClickedListener(int position) {
        LocalSongOptionsDialog localSongOptionsDialog = new LocalSongOptionsDialog(data.get(position).getTitle());
        localSongOptionsDialog.show(getChildFragmentManager(), localSongOptionsDialog.getTag());
        localSongOptionsDialog.setOnActionListener(new LocalSongOptionsDialog.OnActionListener() {
            @Override
            public void onRemove() {
                PlaylistSong playlistSong = new PlaylistSong(data.get(position).getId(), FAVORITE_PLAYLIST_ID);
                AppDatabase.getInstance(getContext()).getPlaylistSongDao().delete(playlistSong);

                localSongOptionsDialog.dismiss();

                data.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, data.size());
                Toast.makeText(getActivity(), getResources().getString(R.string.remove_from_favorite), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onShare() {
                if (getActivity() != null) {
                    Helper.shareSong(data.get(position), new WeakReference(getActivity()));

                }
            }
        });
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
                ApiBuilder.getInstance().increasePlay(list.get(i).getId());
            }
        }

    }
}
