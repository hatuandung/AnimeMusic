package com.example.animemusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.SongAdapter;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.ListSongResp;
import com.example.animemusic.models.Playlist;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.Helper;
import com.example.animemusic.view.EmptyImageView;
import com.example.animemusic.view.LocalSongOptionsDialog;
import com.example.animemusic.view.PlaylistListDialog;
import com.example.animemusic.view.SaveMyPlaylistDialog;
import com.example.animemusic.view.SongOptionsDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.animemusic.utils.Constant.FAVORITE_PLAYLIST_ID;

public class AnimePlaylistFragment extends BasePlaylistFragment implements SongAdapter.SongItemListener, Callback<ListSongResp> {

    private int mId;
    private String mName;
    private String mThumbnail;
    private RelativeLayout emptyContainer;
    public TextView txtEmpyLocal;
    public LinearLayout info_container;
    private boolean isLocal = false;


    private ArrayList<Song> songs = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        txtEmpyLocal = view.findViewById(R.id.txt_local_empty);
        emptyContainer = view.findViewById(R.id.empty_container);
        info_container = view.findViewById(R.id.info_container);

        if (songs.size() == 0 && isLocal){
            txtEmpyLocal.setVisibility(View.VISIBLE);
            info_container.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void setUpTitle() {

    }

    @Override
    public void getData(SongAdapter songAdapter) {
        songAdapter.setSongs(songs);
        onPlaylistLoaded(songs);
        playlistTitle.setText(mName);
        songAdapter.setListener(this);

    }

    public void getLocal( int id, ArrayList<Song> data, String name) {
        mId = id;
        songs.clear();
        songs.addAll(data);
        mName = name;
        isLocal = true;
    }

    public void getInfo(int id, String name, String thumbnail) {
        mId = id;
        mName = name;
        mThumbnail = thumbnail;
        ApiBuilder.getInstance().getSongsByPlaylist(mId, 200, 0).enqueue(this);
    }

    @Override
    public void onPlaylistLoaded(List<Song> list) {
        super.onPlaylistLoaded(list);
        RelativeLayout relativeLayout;
        miniLoading.setVisibility(View.GONE);
        if (list.size() == 0 && (relativeLayout = emptyContainer) != null) {
            relativeLayout.setVisibility(View.VISIBLE);
            ((EmptyImageView) emptyContainer.findViewById(R.id.empty)).setState(EmptyImageView.EmptyState.EMPTY);
            TextView textView = emptyContainer.findViewById(R.id.info);
            if (textView != null) {
                textView.setText("This playlist is empty");
            }
        }
        if (playlistThumb == null && list.size() > 0) {
            Glide.with(getActivity()).load(songs.get(0).getArtworkUrl()).centerCrop().into(playlistThumb);
        }

    }

    @Override
    public void onSongClickedListener(int position) {
        handleSaveQueueAndPlaySong(songs, position);

        if (getActivity() != null && (getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    @Override
    public void onSongLongClickedListener(int position) {

        if (isLocal == true && songs.size() != 0 ){
            LocalSongOptionsDialog localSongOptionsDialog = new LocalSongOptionsDialog(songs.get(position).getTitle());
            localSongOptionsDialog.show(getChildFragmentManager(), localSongOptionsDialog.getTag());
            localSongOptionsDialog.setOnActionListener(new LocalSongOptionsDialog.OnActionListener() {
                @Override
                public void onRemove() {
                    int count = AppDatabase.getInstance(getActivity()).getPlaylistDao().getSongCount(mId);
                    PlaylistSong playlistSong = new PlaylistSong(songs.get(position).getId(), mId);
                    AppDatabase.getInstance(getContext()).getPlaylistSongDao().delete(playlistSong);
                    count = count - 1;
                    AppDatabase.getInstance(getActivity()).getPlaylistDao().updateCountById(count, mId);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.getFmProfile().getFmTabPlaylist().getData();

                    localSongOptionsDialog.dismiss();

                    songs.remove(position);
                    songAdapter.notifyItemRemoved(position);
                    songAdapter.notifyItemRangeChanged(position, songs.size());
                    Toast.makeText(getActivity(), getResources().getString(R.string.remove_from_playlist) + mName, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onShare() {
                    if (getActivity() != null) {
                        Helper.shareSong(songs.get(position), new WeakReference(getActivity()));

                    }
                }
            });
        } else {
            Song song = songs.get(position);
            String title = song.getTitle();

            SongOptionsDialog songOptionsDialog = new SongOptionsDialog(title);
            songOptionsDialog.show(getChildFragmentManager(), songOptionsDialog.getTag());
            songOptionsDialog.setOnActionListener(new SongOptionsDialog.OnActionListener() {
                @Override
                public void onAddTo() {
                    songOptionsDialog.dismiss();
                    PlaylistListDialog playlistListDialog = new PlaylistListDialog(title);
                    playlistListDialog.show(getChildFragmentManager(), getTag());
                    playlistListDialog.setOnActionListener(new PlaylistListDialog.OnActionListener() {
                        @Override
                        public void onAddToPlaylist(Playlist playlist) {
                            try {
                                AppDatabase.getInstance(getActivity()).getSongDao().insert(song);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                int count = AppDatabase.getInstance(getActivity()).getPlaylistDao().getSongCount(playlist.getId());

                                PlaylistSong playlistSong = new PlaylistSong(song.getId(), playlist.getId());
                                AppDatabase.getInstance(getActivity()).getPlaylistSongDao().insert(playlistSong);

                                count = count + 1;
                                playlist.setSongsCount(count);
                                AppDatabase.getInstance(getActivity()).getPlaylistDao().update(playlist);
                                Toast.makeText(getActivity(), getResources().getString(R.string.added_to_my_playlist), Toast.LENGTH_SHORT).show();

                            } catch (Exception ex) {
                                Toast.makeText(getActivity(), getString(R.string.existed), Toast.LENGTH_SHORT).show();
                            }
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.getFmProfile().getFmTabPlaylist().getData();
                            playlistListDialog.dismiss();
                        }

                        @Override
                        public void onCreateNewPlaylist() {
                            final SaveMyPlaylistDialog saveMyPlaylistDialog = new SaveMyPlaylistDialog();
                            saveMyPlaylistDialog.show(getChildFragmentManager(), saveMyPlaylistDialog.getTag());
                            saveMyPlaylistDialog.setOnActionListener(new SaveMyPlaylistDialog.OnActionListener() {
                                @Override
                                public void onCancel() {
                                    saveMyPlaylistDialog.dismiss();
                                }

                                @Override
                                public void onOk(String str) {
                                    Playlist playlist = new Playlist();
                                    playlist.setName(str);
                                    try {
                                        AppDatabase.getInstance(getActivity()).getPlaylistDao().insert(playlist);

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    mainActivity.getFmProfile().getFmTabPlaylist().getData();

                                    saveMyPlaylistDialog.dismiss();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onAddToFavorite() {
                    String songId = songs.get(position).getId();

                    try {
                        AppDatabase.getInstance(getContext()).getSongDao().insert(songs.get(position));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        AppDatabase.getInstance(getContext()).getPlaylistSongDao().insert(new PlaylistSong(songId, FAVORITE_PLAYLIST_ID));
                        Toast.makeText(getActivity(), getResources().getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), getString(R.string.existed), Toast.LENGTH_SHORT).show();
                    }
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.getFmProfile().getFmTabFavorite().getData();
                    songOptionsDialog.dismiss();
                }

                @Override
                public void onShare() {
                    if (getActivity() != null) {
                        Helper.shareSong(song, new WeakReference(getActivity()));
                    }
                }
            });
        }
    }

    @Override
    public void onResponse(Call<ListSongResp> call, Response<ListSongResp> response) {
        ListSongResp listSongResp = response.body();
        songs.addAll(listSongResp.getSongs());
        Log.e("onResponse: ", String.valueOf(songs.size()));
        songAdapter.setSongs(songs);
        songAdapter.setListener(this);
        onPlaylistLoaded(songs);
        Glide.with(getContext()).load(mThumbnail).centerCrop().placeholder(R.drawable.placeholder_song).into(playlistThumb);
        playlistTitle.setText(mName);
//        miniLoading.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Call<ListSongResp> call, Throwable t) {

    }
}
