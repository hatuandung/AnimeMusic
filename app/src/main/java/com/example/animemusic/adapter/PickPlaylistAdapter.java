package com.example.animemusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.models.Playlist;

import java.util.ArrayList;

public class PickPlaylistAdapter extends RecyclerView.Adapter<PickPlaylistAdapter.PlaylistHolder> {

    private LayoutInflater inflater;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private PlaylistItemListener listener;

    public void setListener(PlaylistItemListener listener) {
        this.listener = listener;
    }

    public PickPlaylistAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PickPlaylistAdapter.PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_playlist_pick, parent, false);
        return new PlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickPlaylistAdapter.PlaylistHolder holder, int position) {
        holder.bindView(playlists.get(position));

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlaylistClickedListener(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;

        public PlaylistHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
        }

        public void bindView(Playlist playlist) {
            txtTitle.setText(playlist.getName());
        }
    }

    public interface PlaylistItemListener {
        void onPlaylistClickedListener(int position);
    }
}
