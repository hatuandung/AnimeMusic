package com.example.animemusic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.adapter.PickPlaylistAdapter;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.models.Playlist;
import com.example.animemusic.utils.SharedPrefHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class PlaylistListDialog extends BottomSheetDialogFragment implements PickPlaylistAdapter.PlaylistItemListener {
    private OnActionListener listener;
    private String title;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    PickPlaylistAdapter pickPlaylistAdapter ;

    public interface OnActionListener {
        void onAddToPlaylist(Playlist playlist);

        void onCreateNewPlaylist();
    }

    public PlaylistListDialog(String str) {
        this.title = str;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.listener = onActionListener;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_playlist_list, viewGroup, false);
        TextView textView = inflate.findViewById(R.id.title);
        String str = this.title;
        if (str != null) {
            textView.setText(str);
        }
        inflate.findViewById(R.id.new_playlist).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                OnActionListener onActionListener = listener;
                if (onActionListener != null) {
                    onActionListener.onCreateNewPlaylist();

                }
            }
        });
        RecyclerView recyclerView = inflate.findViewById(R.id.playlist_list_rv);
        recyclerView.setHasFixedSize(true);
        pickPlaylistAdapter = new PickPlaylistAdapter(getLayoutInflater());

        recyclerView.setAdapter(pickPlaylistAdapter);
//        playlists.clear();
//        playlists.addAll(AppDatabase.getInstance(getActivity()).getPlaylistDao().getAllExceptFavorite());
//        pickPlaylistAdapter.setPlaylists(playlists);
        setData();
        pickPlaylistAdapter.setListener(this);
        return inflate;
    }

    public void setData(){
        playlists.clear();
        playlists.addAll(AppDatabase.getInstance(getActivity()).getPlaylistDao().getAllExceptFavorite());
        if (pickPlaylistAdapter != null) {
            pickPlaylistAdapter.setPlaylists(playlists);
        }
    }

    @Override
    public void onPlaylistClickedListener(int position) {
        AppStats appStats;
        if (!(getActivity() == null || (appStats = SharedPrefHelper.getInstance(getActivity()).getAppStats()) == null)) {
            appStats.setNumberOfAddToPlaylist(appStats.getNumberOfAddToPlaylist() + 1);
            appStats.setAddSongToLocalCounter(appStats.getAddSongToLocalCounter() + 1);
            SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);
        }


        OnActionListener onActionListener = this.listener;
        if (onActionListener != null) {
            onActionListener.onAddToPlaylist(playlists.get(position));
        }

    }


}
