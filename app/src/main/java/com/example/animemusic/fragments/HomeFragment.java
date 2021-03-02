package com.example.animemusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.BannerAdapter;
import com.example.animemusic.adapter.PlaylistAdapter;
import com.example.animemusic.adapter.SongAdapter;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.interfaces.CommunicationInterface;
import com.example.animemusic.models.Home;
import com.example.animemusic.models.ListPlaylistResp;
import com.example.animemusic.models.ListSongResp;
import com.example.animemusic.models.Playlist;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.Color;
import com.example.animemusic.utils.Helper;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.example.animemusic.view.PlaylistListDialog;
import com.example.animemusic.view.SaveMyPlaylistDialog;
import com.example.animemusic.view.SongOptionsDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.animemusic.utils.Constant.FAVORITE_PLAYLIST_ID;

public class HomeFragment extends BaseFragment implements SongAdapter.SongItemListener {
    //top 100 song
    private ArrayList<Song> songs = new ArrayList<>();
    private RecyclerView rvTop100;
    private SongAdapter songAdapter;
    private Button btnTop100;
    //playlist anime
    private ArrayList<Playlist> animePlaylist = new ArrayList<>();
    private RecyclerView rvAnime;
    private PlaylistAdapter animeAdapter;
    private Button btnAnime;
    // playlist for you
    private ArrayList<Playlist> forYouPlaylist = new ArrayList<>();
    private RecyclerView rvForYou;
    private PlaylistAdapter forYouAdapter;
    private Button btnForYou;
    //banner
    private EnchantedViewPager enchantedViewPager;
    private BannerAdapter bannerAdapter;
    private ArrayList<Playlist> banners = new ArrayList<>();

    public RelativeLayout bannerContainer;
    public SpinKitView loading;
    private CommunicationInterface communicationInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.communicationInterface = (CommunicationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement onViewSelected!");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTop100();
        initTop100();
        initPlaylistAnime();
        initPlaylistForYou();
        initBanner();

        loading = findViewByID(R.id.loading);
    }

    public void initBanner() {
        if (getActivity() != null) {
            enchantedViewPager = findViewByID(R.id.viewPager_home);
            bannerContainer = findViewByID(R.id.banner_container);
            getBanner();
        }
    }

    public void getBanner() {
        ApiBuilder.getInstance().getHome().enqueue(new Callback<Home>() {
            @Override
            public void onResponse(Call<Home> call, Response<Home> response) {
                banners.clear();
                Home home = response.body();
                banners.addAll(home.getBanners());

                bannerAdapter = new BannerAdapter(getContext(), banners);
                enchantedViewPager.removeScale();
                enchantedViewPager.setPageMargin(45);
                enchantedViewPager.setAdapter(bannerAdapter);
                ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (banners.size() != 0) {
                            if (getContext() != null) {
                                Color.loadBgBannerAverage((int) getContext().getResources().getDimension(R.dimen.image_size), bannerContainer, banners.get(position).getThumbnailUrl());
                            }
                        }
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                };

                enchantedViewPager.addOnPageChangeListener(listener);
                enchantedViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPageSelected(enchantedViewPager.getCurrentItem());
                    }
                });

                bannerAdapter.notifyDataSetChanged();

                bannerAdapter.setOnClickListener(new BannerAdapter.BannerAdapterListener() {
                    @Override
                    public void onClick(int i) {

                        int id = banners.get(i).getId();
                        String name = banners.get(i).getName();
                        String thumbnail = banners.get(i).getThumbnailUrl();

                        communicationInterface.getAnimeInfo(id, name, thumbnail);
                    }
                });
            }

            @Override
            public void onFailure(Call<Home> call, Throwable t) {

            }
        });
    }

    public void initTop100() {
        rvTop100 = findViewByID(R.id.top100_list);
        songAdapter = new SongAdapter(getLayoutInflater());
        rvTop100.setAdapter(songAdapter);
        btnTop100 = findViewByID(R.id.see_all_chart);
        btnTop100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Top100Fragment top100Fragment = new Top100Fragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .add(R.id.main_pager, top100Fragment, "fmTop100")
//                        .addToBackStack(null)
//                        .commit();

                getChildFragmentManager().beginTransaction()
                        .add(R.id.main_pager, top100Fragment, "fmTop100")
                        .addToBackStack("fmTop100")
                        .commit();

            }
        });
        songAdapter.setListener(this);

    }

    public void getTop100() {
        ApiBuilder.getInstance().getChart(200, 0).enqueue(new Callback<ListSongResp>() {
            @Override
            public void onResponse(Call<ListSongResp> call, Response<ListSongResp> response) {
                songs.clear();
                ArrayList<Song> data = new ArrayList<>();
                ListSongResp listSongResp = response.body();
                data.addAll(listSongResp.getSongs());
                for (int i = 0; i < 5; i++) {
                    songs.add(data.get(i));
                }

                if (songs != null) {
                    songAdapter.setSongs(songs);
                }
            }

            @Override
            public void onFailure(Call<ListSongResp> call, Throwable t) {
                Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initPlaylistAnime() {
        rvAnime = findViewByID(R.id.anime_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvAnime.setLayoutManager(layoutManager);

        animeAdapter = new PlaylistAdapter(getLayoutInflater());
        rvAnime.setAdapter(animeAdapter);
        getAnimePlaylist();

        animeAdapter.setListener(new PlaylistAdapter.PlaylistItemListener() {
            @Override
            public void onPlaylistClickedListener(int position) {

                int id = animePlaylist.get(position).getId();
                String name = animePlaylist.get(position).getName();
                String thumbnail = animePlaylist.get(position).getThumbnailUrl();

                communicationInterface.getAnimeInfo(id, name, thumbnail);
            }
        });

        btnAnime = findViewByID(R.id.see_all_playlist);

        btnAnime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPlaylistFragment allPlaylistFragment = new AllPlaylistFragment();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.main_pager, allPlaylistFragment, "AnimeFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getAnimePlaylist() {
        ApiBuilder.getInstance().getPlaylists(100, 0).enqueue(new Callback<ListPlaylistResp>() {
            @Override
            public void onResponse(Call<ListPlaylistResp> call, Response<ListPlaylistResp> response) {
                animePlaylist.clear();
                ArrayList<Playlist> data = new ArrayList<>();
                ListPlaylistResp listPlaylistResp = response.body();
                data.addAll(listPlaylistResp.getPlaylists());
                for (int i = 0; i < 5; i++) {
                    animePlaylist.add(data.get(i));
                }

                if (animePlaylist != null) {
                    animeAdapter.setPlaylists(animePlaylist);
                }
            }

            @Override
            public void onFailure(Call<ListPlaylistResp> call, Throwable t) {

            }
        });
    }

    private void initPlaylistForYou() {
        rvForYou = findViewByID(R.id.feature_playlist_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvForYou.setLayoutManager(layoutManager);

        forYouAdapter = new PlaylistAdapter(getLayoutInflater());
        rvForYou.setAdapter(forYouAdapter);
        btnForYou = findViewByID(R.id.see_all_feature_playlist);
        getForYouPlaylist();

        forYouAdapter.setListener(new PlaylistAdapter.PlaylistItemListener() {
            @Override
            public void onPlaylistClickedListener(int position) {
                int id = forYouPlaylist.get(position).getId();
                String name = forYouPlaylist.get(position).getName();
                String thumbnail = forYouPlaylist.get(position).getThumbnailUrl();

                communicationInterface.getAnimeInfo(id, name, thumbnail);
            }
        });

        btnForYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllForYouFragment allForYouFragment = new AllForYouFragment();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.main_pager, allForYouFragment, "ForYou")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    private void getForYouPlaylist() {
        ApiBuilder.getInstance().getFeaturedPlaylists(100, 0).enqueue(new Callback<ListPlaylistResp>() {
            @Override
            public void onResponse(Call<ListPlaylistResp> call, Response<ListPlaylistResp> response) {
                forYouPlaylist.clear();
                ArrayList<Playlist> data = new ArrayList<>();
                ListPlaylistResp listPlaylistResp = response.body();
                data.addAll(listPlaylistResp.getPlaylists());
                for (int i = 0; i < 5; i++) {
                    forYouPlaylist.add(data.get(i));
                }

                if (forYouPlaylist != null) {
                    forYouAdapter.setPlaylists(forYouPlaylist);
                }
            }

            @Override
            public void onFailure(Call<ListPlaylistResp> call, Throwable t) {

            }
        });

    }

    // click song top 100
    @Override
    public void onSongClickedListener(int position) {
        SharedPrefHelper.getInstance(getContext()).addRecentSong(songs.get(position));

        handleSaveQueueAndPlaySong(songs, position);

        if (getActivity() != null && (getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
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
                                playlistListDialog.setData();
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
            }
        }
    }
}
