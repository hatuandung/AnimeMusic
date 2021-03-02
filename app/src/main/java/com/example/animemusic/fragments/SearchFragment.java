package com.example.animemusic.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.SongAdapter;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.Config;
import com.example.animemusic.models.Playlist;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.Helper;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.example.animemusic.view.PlaylistListDialog;
import com.example.animemusic.view.SaveMyPlaylistDialog;
import com.example.animemusic.view.SongOptionsDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.animemusic.utils.Constant.FAVORITE_PLAYLIST_ID;

public class SearchFragment extends BaseFragment implements  Callback<List<Song>>, SearchView.OnQueryTextListener {

    private TextView infoText;
    private boolean isDirty = false;
    private SpinKitView loading;
    private SongAdapter songAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Song> data = new ArrayList<>();
    private RelativeLayout relativeLayout;
    private SearchView searchView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_search;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews() {
        loading = findViewByID(R.id.loading);
        infoText = findViewByID(R.id.text_info);
        infoText.setText(getText(R.string.search));

        isDirty = false;
        toolbar = findViewByID(R.id.toolbar);
        toolbar.setTitle(R.string.title_search);
        setUpSearchRV();
        setUpSearchView();
    }

    private void setUpSearchView() {
        relativeLayout = findViewByID(R.id.search_container);
        searchView = findViewByID(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Anime, songs, or artists");
        searchView.setSubmitButtonEnabled(true);


        trimChildMargins(searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                autoSubmitQuery(searchView, query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        searchView.setOnQueryTextListener(this);


        searchView.findViewById(R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.requestFocus();
            }
        });

        findViewByID(R.id.search_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songAdapter.setSongs(new ArrayList<>());
                searchView.setQuery(null, false);
                infoText.setText(getText(R.string.search));
            }
        });

    }

    private void setUpSearchRV() {
        recyclerView = findViewByID(R.id.item_rv);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(getLayoutInflater());
        recyclerView.setAdapter(songAdapter);
    }


    private static void trimChildMargins(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                trimChildMargins((ViewGroup) childAt);
            }
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.leftMargin = 0;
                marginLayoutParams.rightMargin = 0;
            }
        }
    }
    public void autoSubmitQuery(SearchView searchView, String str) {
        Config config = SharedPrefHelper.getInstance(getActivity()).getConfig();
        if (config == null) {
            Toast.makeText(getActivity(), getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
            return;
        }
        loading.setVisibility(View.VISIBLE);
        ApiBuilder.getInstance().search(str, 200, 0).enqueue(this);
        searchView.clearFocus();
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
                ApiBuilder.getInstance().increasePlay(list.get(i).getId());
            }
        }
    }

    @Override
    public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
        loading.setVisibility(View.INVISIBLE);
        data.clear();
        data = (ArrayList<Song>) response.body();
        songAdapter.setSongs(data);
        songAdapter.setListener(new SongAdapter.SongItemListener() {
            @Override
            public void onSongClickedListener(int position) {
                //Toast.makeText(getContext(), data.get(position).getStreamUrl(), Toast.LENGTH_SHORT).show();
                handleSaveQueueAndPlaySong(data, position);
                if (getActivity() != null && (getActivity() instanceof MainActivity)) {
                    ((MainActivity) getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }

            @Override
            public void onSongLongClickedListener(int position) {
                Song song = data.get(position);
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
                        String songId = data.get(position).getId();

                        try {
                            AppDatabase.getInstance(getContext()).getSongDao().insert(data.get(position));
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
        });
        if (data.size() == 0){
            infoText.setVisibility(View.VISIBLE);
            infoText.setText(getText(isDirty ? R.string.empty_search : R.string.search));

        }else {
            infoText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFailure(Call<List<Song>> call, Throwable t) {
        Toast.makeText(getActivity(), getText(R.string.something_wrong), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loading.setVisibility(View.VISIBLE);
        ApiBuilder.getInstance().search(query, 200, 0).enqueue(this);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
