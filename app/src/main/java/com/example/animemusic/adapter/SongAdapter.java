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
import com.example.animemusic.models.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    private LayoutInflater inflater;
    private ArrayList<Song> songs ;
    private SongItemListener listener;

    public void setSongs(ArrayList<Song> songs) {

        this.songs = songs;
        notifyDataSetChanged();
    }

    public void setListener(SongItemListener listener) {
        this.listener = listener;
    }

    public SongAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_song, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        holder.bindView(songs.get(position));

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSongClickedListener(position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onSongLongClickedListener(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        ImageView imgThumbnail;
        ImageView imgOptions;
        TextView txtTitle;
        TextView txtDescription;

        public SongHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.thumbnail);
            imgOptions = itemView.findViewById(R.id.options);
            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
        }

        public void bindView(Song song) {
            Glide.with(itemView).load(song.getArtworkUrl()).centerCrop().into(imgThumbnail);

            txtTitle.setText(song.getTitle());
            txtDescription.setText(song.getDescription());

            imgOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongItemListener songItemListener = listener;
                    if (songItemListener != null) {
                        songItemListener.onSongLongClickedListener(getAdapterPosition());
                    }

                }
            });
        }
    }

    public interface SongItemListener {
        void onSongClickedListener(int position);

        void onSongLongClickedListener(int position);
    }
}
