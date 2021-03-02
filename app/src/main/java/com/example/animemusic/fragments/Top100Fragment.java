package com.example.animemusic.fragments;

import android.os.Bundle;
import android.widget.Toast;

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
import com.example.animemusic.view.PlaylistListDialog;
import com.example.animemusic.view.SaveMyPlaylistDialog;
import com.example.animemusic.view.SongOptionsDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.animemusic.utils.Constant.FAVORITE_PLAYLIST_ID;

public class Top100Fragment extends BasePlaylistFragment implements SongAdapter.SongItemListener {

    public ArrayList<Song> songs = new ArrayList<>();

    @Override
    public void setUpTitle() {
        playlistTitle.setText(getResources().getText(R.string.title_playlist_chart));
    }

    @Override
    public void getData(SongAdapter songAdapter) {
        ApiBuilder.getInstance().getChart(200, 0).enqueue(new Callback<ListSongResp>() {
            @Override
            public void onResponse(Call<ListSongResp> call, Response<ListSongResp> response) {
                songs.clear();
                ListSongResp listSongResp = response.body();
                songs.addAll(listSongResp.getSongs());

                if (songs != null) {
                    songAdapter.setSongs(songs);
                    Glide.with(getActivity()).load(songs.get(0).getArtworkUrl()).centerCrop().into(playlistThumb);
                }
            }

            @Override
            public void onFailure(Call<ListSongResp> call, Throwable t) {
                Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
            }
        });

        songAdapter.setListener(this);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onSongClickedListener(int position) {
        handleSaveQueueAndPlaySong(songs, position);

        if (getActivity() != null && (getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }

        Toast.makeText(getActivity(), songs.get(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongLongClickedListener(int position) {
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
