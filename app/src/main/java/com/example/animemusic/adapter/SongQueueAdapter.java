package com.example.animemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.animemusic.R;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.SharedPrefHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongQueueAdapter extends RecyclerView.Adapter<SongQueueAdapter.SongQueueHolder> {
    private StartDragListener startDragListener;
    private ArrayList<Song> songs = new ArrayList<>();
    private SongQueueItemListener listener;
    private Context context;

    public SongQueueAdapter(Context context) {
        this.context = context;
    }

    public void setStartDragListener(StartDragListener startDragListener) {
        this.startDragListener = startDragListener;
    }

    public void setListener(SongQueueItemListener listener) {
        this.listener = listener;
    }

    public void setSongs(ArrayList<Song> songs) {
        notifyDataSetChanged();
        this.songs = songs;
    }

    public void onMove(int i, int i2) {
        Song song = this.songs.get(SharedPrefHelper.getInstance(this.context).getQueueIndex(-1));
        if (i < i2) {
            int i3 = i;
            while (i3 < i2) {
                int i4 = i3 + 1;
                Collections.swap(this.songs, i3, i4);
                i3 = i4;
            }
        } else {
            for (int i5 = i; i5 > i2; i5--) {
                Collections.swap(this.songs, i5, i5 - 1);
            }
        }
        notifyItemMoved(i, i2);
        this.startDragListener.postDrag(this.songs, this.songs.indexOf(song));
    }


    @NonNull
    @Override
    public SongQueueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_queue, parent, false);
        return new SongQueueAdapter.SongQueueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongQueueHolder holder, int position) {
        holder.bindView(songs.get(position));
        if (listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onQueueClickedListener(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }


    public interface StartDragListener {
        void postDrag(List<Song> list, int i);

        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }


    public class SongQueueHolder extends RecyclerView.ViewHolder{

        private View container;
        private TextView txtTitile;
        private TextView txtDescription;
        private ImageView imgMove;
        private ImageView imgThumbnail;


        public SongQueueHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            txtTitile = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            imgMove = itemView.findViewById(R.id.move_song);
            imgThumbnail = itemView.findViewById(R.id.thumbnail);

        }

        public void bindView(Song song){
            txtTitile.setText(song.getTitle());
            txtDescription.setText(song.getDescription());
            Glide.with(itemView).load(song.getArtworkUrl()).centerCrop().placeholder(R.drawable.placeholder_song).into(imgThumbnail);

            imgMove.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() != 0) {
                        return false;
                    }
                    startDragListener.requestDrag(SongQueueHolder.this);
                    return false;
                }
            });

            if (SharedPrefHelper.getInstance(itemView.getContext()).getQueueIndex(-1) == SongQueueHolder.this.getLayoutPosition()) {
                container.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_current_play_gradient));
            } else {
                container.setBackgroundResource(0);
            }
        }

    }

    public interface SongQueueItemListener {
        void onQueueClickedListener(int position);
    }


}
