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

public class AllPlaylistAdapter extends RecyclerView.Adapter<AllPlaylistAdapter.AllPlaylistViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private AllPlaylistItemListener listener;

    public void setListener(AllPlaylistItemListener listener) {
        this.listener = listener;
    }

    public AllPlaylistAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_playlist, parent, false);
        return new AllPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllPlaylistViewHolder holder, int position) {
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

    public class AllPlaylistViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgThumbnail;
        private TextView txtTitle;
        private TextView txtNum;

        public AllPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.thumbnail);
            txtTitle = itemView.findViewById(R.id.title);
            txtNum = itemView.findViewById(R.id.num);

        }

        public void bindView(Playlist playlist) {
            Glide.with(itemView).load(playlist.getThumbnailUrl()).centerCrop().into(imgThumbnail);
            txtTitle.setText(playlist.getName());
            //txtNum.setText(playlist.getSongsCount());
        }
    }

    public interface AllPlaylistItemListener {
        void onPlaylistClickedListener(int position);

    }
}
