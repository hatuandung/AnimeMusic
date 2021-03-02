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

public class TabPlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private LayoutInflater inflater;
    private ArrayList<Playlist> data = new ArrayList();
    public HeaderListener headerListener;
    public ItemListener itemListener;

    public TabPlaylistAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setHeaderListener(HeaderListener headerListener) {
        this.headerListener = headerListener;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setData(ArrayList<Playlist> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            View view = inflater.inflate(R.layout.row_playlist_header, parent,false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.row_playlist_local, parent, false);
            return new ItemHolder(view);
        }
        else return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            if (headerListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        headerListener.onHeaderClicked();
                    }
                });
            }
        }else if (holder instanceof ItemHolder){
            ((ItemHolder) holder).bindView(data.get(position));
            if (itemListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemListener.onItemClicked(position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        itemListener.onItemLongClicked(position);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        }else return TYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView thumbnail;

        HeaderViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        View container;
        TextView numberOfSong;
        ImageView options;
        ImageView thumbnail;
        TextView title;

        ItemHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
            thumbnail = view.findViewById(R.id.thumbnail);
            title = view.findViewById(R.id.title);
            options = view.findViewById(R.id.options);
            numberOfSong = view.findViewById(R.id.num);
        }

        public void bindView(Playlist playlist){
            title.setText(playlist.getName());

            Glide.with(itemView).load(playlist.getThumbnailUrl()).centerCrop().into(thumbnail);


            if (numberOfSong != null) {
                String str = playlist.getSongsCount() > 1 ? "Songs" : "Song";
                numberOfSong.setText(playlist.getSongsCount() + " " + str);
            }

            options.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ItemListener listener = itemListener;
                    listener.onItemLongClicked(getAdapterPosition());
                }
            });

        }
    }

    public interface HeaderListener{
        void onHeaderClicked();
    }

    public interface ItemListener{
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }

}
