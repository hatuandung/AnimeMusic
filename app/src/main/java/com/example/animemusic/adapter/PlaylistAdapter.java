package com.example.animemusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.animemusic.R;
import com.example.animemusic.models.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>{

    private LayoutInflater inflater;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private PlaylistItemListener listener;

    public void setListener(PlaylistItemListener listener) {
        this.listener = listener;
    }

    public PlaylistAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_playlist_home, parent, false);
        return new PlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        holder.bindView(playlists.get(position));
        if (listener != null){
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

    public class PlaylistHolder extends RecyclerView.ViewHolder{

        private ImageView imgThumbnail;
        private TextView txtTitle;


        public PlaylistHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.thumbnail);
            txtTitle = itemView.findViewById(R.id.title);
        }

        public void bindView(Playlist playlist){
            Glide.with(itemView).load(playlist.getThumbnailUrl()).placeholder(R.drawable.placeholder_song).centerCrop().into(imgThumbnail);
            txtTitle.setText(playlist.getName());
        }
    }

    public interface PlaylistItemListener {
        void onPlaylistClickedListener(int position);

    }
}
