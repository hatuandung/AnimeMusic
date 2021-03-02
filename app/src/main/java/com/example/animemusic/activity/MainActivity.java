package com.example.animemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.animemusic.R;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.fragments.AnimePlaylistFragment;
import com.example.animemusic.fragments.ChatFragment;
import com.example.animemusic.fragments.HomeFragment;
import com.example.animemusic.fragments.ProfileFragment;
import com.example.animemusic.fragments.SearchFragment;
import com.example.animemusic.fragments.TabFavoriteFragment;
import com.example.animemusic.fragments.TabPlaylistFragment;
import com.example.animemusic.interfaces.CommunicationInterface;
import com.example.animemusic.interfaces.NotificationType;
import com.example.animemusic.interfaces.StreamType;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.models.Song;
import com.example.animemusic.service.MusicLibrary;
import com.example.animemusic.service.MusicService;
import com.example.animemusic.service.PlaylistNotification;
import com.example.animemusic.utils.MediaBrowserHelper;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.example.animemusic.view.ExpandSongPlayer;
import com.example.animemusic.view.MiniSongPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CommunicationInterface {

    private HomeFragment fmHome = new HomeFragment();
    private ProfileFragment fmProfile = new ProfileFragment();
    private SearchFragment fmSearch = new SearchFragment();
    private ChatFragment fmChat = new ChatFragment();
    private TabFavoriteFragment fmFavorite = new TabFavoriteFragment();
    private TabPlaylistFragment fmPlaylist = new TabPlaylistFragment();

    private AnimePlaylistFragment fmAnime = new AnimePlaylistFragment();
//    private Top100Fragment fmTop100 = new Top100Fragment();
//    private AllForYouFragment fmForYou = new AllForYouFragment();
//    private AllPlaylistFragment fmAllPlaylist = new AllPlaylistFragment();


    public TabPlaylistFragment getFmPlaylist() {
        return fmPlaylist;
    }

    public HomeFragment getFmHome() {
        return fmHome;
    }

    public ProfileFragment getFmProfile() {
        return fmProfile;
    }

    public SearchFragment getFmSearch() {
        return fmSearch;
    }

    public ChatFragment getFmChat() {
        return fmChat;
    }

    public TabFavoriteFragment getFmFavorite() {
        return fmFavorite;
    }

    private BottomNavigationView bottomNavigation;

    private ExpandSongPlayer expandSongPlayer;
    private MiniSongPlayer miniSongPlayer;
    private RelativeLayout relativeLayout;
    private MediaBrowserHelper mMediaBrowserHelper;
    private SlidingUpPanelLayout slidingUpPanelLayout;


    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {

        bottomNavigation = findViewById(R.id.nav_view);
        bottomNavigation.setOnNavigationItemSelectedListener(MainActivity.this);
        initFragments();
        showFragment(fmHome);

        relativeLayout = findViewById(R.id.player);
        expandSongPlayer = relativeLayout.findViewById(R.id.expand_song_player);
        miniSongPlayer = findViewById(R.id.mini_player);

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset > 0.3d) {
                    miniSongPlayer.setVisibility(View.INVISIBLE);
                    bottomNavigation.setVisibility(View.INVISIBLE);
                    return;
                }
                miniSongPlayer.setVisibility(View.VISIBLE);
                bottomNavigation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setOnClickListener(null);
                }

            }
        });

        miniSongPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout == null) {
                    return;
                }
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }

            }
        });

        MediaBrowserConnection mediaBrowserConnection = new MediaBrowserConnection(this);
        this.mMediaBrowserHelper = mediaBrowserConnection;
        mediaBrowserConnection.registerCallback(new MediaBrowserListener());

        handleOpenFromNotification(getIntent());

    }

    private void initFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_pager, fmHome);
        transaction.add(R.id.main_pager, fmProfile);
        transaction.add(R.id.main_pager, fmChat);
        transaction.add(R.id.main_pager, fmSearch);

//        transaction.add(R.id.main_pager, fmAnime);
//        transaction.add(R.id.main_pager, fmPlaylist);
//        transaction.add(R.id.main_pager, fmAllPlaylist);
//        transaction.add(R.id.main_pager, fmTop100);
//        transaction.add(R.id.main_pager, fmForYou);

        transaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                showFragment(fmHome);
                break;
            case R.id.navigation_profile:
                showFragment(fmProfile);
                break;
            case R.id.navigation_search:
                showFragment(fmSearch);
                break;
            case R.id.navigation_chat:
                showFragment(fmChat);
                break;

        }
        return true;
    }

    private void showFragment(Fragment fmShow) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.hide(fmHome);
        transaction.hide(fmProfile);
        transaction.hide(fmSearch);
        transaction.hide(fmChat);


        transaction.show(fmShow);

        transaction.commit();
    }


    private class MediaBrowserConnection extends MediaBrowserHelper {
        public MediaBrowserConnection(Context context) {
            super(context, MusicService.class);
        }

        @Override
        protected void onConnected(MediaControllerCompat mediaControllerCompat) {
            if (expandSongPlayer != null) {
                expandSongPlayer.setMediaController(mediaControllerCompat);
            }

            if (miniSongPlayer != null) {
                miniSongPlayer.setMediaController(mediaControllerCompat);
            }

        }

        @Override
        protected void onChildrenLoaded(String str, List<MediaBrowserCompat.MediaItem> list) {
            super.onChildrenLoaded(str, list);
            MediaControllerCompat mediaController = getMediaController();
            if (mediaController == null) {
                MainActivity mainActivity = MainActivity.this;
                Toast.makeText(mainActivity, mainActivity.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
            for (MediaBrowserCompat.MediaItem description : list) {
                mediaController.addQueueItem(description.getDescription());
            }
            mediaController.getTransportControls().prepare();
        }

    }

    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        private MediaBrowserListener() {
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            if (metadata != null) {
                String string = metadata.getString(MusicLibrary.METADATA_KEY_MEDIA_STREAM_TYPE);
                if (expandSongPlayer != null) {
                    if (string.equals(StreamType.SOUNDCLOUD.getValue()) || string.equals(StreamType.NORMAL.getValue())) {
                        expandSongPlayer.setVisibility(View.VISIBLE);
                        return;
                    }
                    expandSongPlayer.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            AppStats appStats;
            if (playbackStateCompat != null && playbackStateCompat.getState() == STATE_PAUSED && (appStats = SharedPrefHelper.getInstance(MainActivity.this).getAppStats()) != null) {
                appStats.setNumberOfPaused(appStats.getNumberOfPaused() + 1);
                appStats.setPausedCounter(appStats.getPausedCounter() + 1);
                SharedPrefHelper.getInstance(MainActivity.this).saveAppStats(appStats);

            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> list) {
            super.onQueueChanged(list);
        }
    }


    @Override
    public void onBackPressed() {
        SlidingUpPanelLayout slidingPanelLayout = slidingUpPanelLayout;
//        if (slidingPanelLayout == null || slidingPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
//            super.onBackPressed();
//        } else {
//            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        }

        if (slidingPanelLayout != null || slidingPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            FragmentManager fm = getSupportFragmentManager();
            for (Fragment frag : fm.getFragments()) {
                if (frag.isVisible()) {
                    FragmentManager childFm = frag.getChildFragmentManager();
                    if (childFm.getBackStackEntryCount() > 0) {
                        childFm.popBackStack();
                        return;
                    }
                }
            }
        }
        if (slidingPanelLayout == null || slidingPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED){
            super.onBackPressed();
        }




    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowserHelper.onStop();
        ExpandSongPlayer expandSongPlayer2 = expandSongPlayer;
        if (expandSongPlayer2 != null) {
            expandSongPlayer2.disconnect();
        }

        MiniSongPlayer miniSongPlayer2 = miniSongPlayer;
        if (miniSongPlayer2 != null) {
            miniSongPlayer2.disconnect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueueManager.getInstance().clear();
        expandSongPlayer = null;
        miniSongPlayer = null;
    }


    @Override
    public void getAnimeInfo(int id, String name, String thumbnail) {
        AnimePlaylistFragment animePlaylistFragment = new AnimePlaylistFragment();

        fmHome.getChildFragmentManager().beginTransaction()
                .add(R.id.main_pager, animePlaylistFragment, "getAnimeInfo")
                .addToBackStack(null)
                .commit();


        if (animePlaylistFragment != null) {
            animePlaylistFragment.getInfo(id, name, thumbnail);
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getLocalPlaylist(int id, String name) {
        AnimePlaylistFragment animePlaylistFragment = new AnimePlaylistFragment();

        fmProfile.getChildFragmentManager().beginTransaction()
                .add(R.id.main_pager, animePlaylistFragment, "getLocalPlaylist")
                .addToBackStack(null)
                .commit();

        if (animePlaylistFragment != null) {

            ArrayList<Song> songs = new ArrayList<>();
            songs.addAll(AppDatabase.getInstance(MainActivity.this).getSongDao().getByPlaylistId(id, 200, 0));
            animePlaylistFragment.getLocal(id, songs, name);
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleOpenFromNotification(Intent intent) {
        String string;
        try {
            Bundle extras = intent.getExtras();
            if (extras != null && (string = extras.getString("type")) != null) {
                if (string.equals(NotificationType.PLAYLIST.getValue())) {
                    openPlaylist(extras);
                } else if (string.equals(NotificationType.SONG.getValue())) {
                    openSong(extras);
                }
            }
        } catch (Exception unused) {
        }
    }

    private void openPlaylist(Bundle bundle) {
        Bundle bundle2 = new Bundle();
        String string = bundle.getString(PlaylistNotification.PLAYLIST_ID);
        String string2 = bundle.getString(PlaylistNotification.PLAYLIST_NAME);
        String string3 = bundle.getString(PlaylistNotification.PLAYLIST_THUMBNAIL);
        if (string != null) {
            bundle2.putInt("id", Integer.parseInt(string));
            bundle2.putString("name", string2);
            bundle2.putString("thumbnail", string3);
        }
    }

    private void openSong(Bundle bundle) {
        String string = bundle.getString("id");
        String string2 = bundle.getString("title");
        String string3 = bundle.getString("description");
        String string4 = bundle.getString("stream_url");
        String string5 = bundle.getString("stream_type");
        String string7 = bundle.getString("artwork_url");
        String string8 = bundle.getString("duration");
        Song song = new Song();
        song.setId(string);
        song.setTitle(string2);
        song.setDescription(string3);
        song.setStreamUrl(string4);
        song.setStreamType(string5);
        song.setArtworkUrl(string7);
        song.setDuration(Integer.parseInt(string8));
        ArrayList arrayList = new ArrayList();
        arrayList.add(song);
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        MiniSongPlayer miniSongPlayer2 = miniSongPlayer;
        if (miniSongPlayer2 != null) {
            miniSongPlayer2.setVisibility(View.INVISIBLE);
        }
        BottomNavigationView bottomNavigationView2 = bottomNavigation;
        if (bottomNavigationView2 != null) {
            bottomNavigationView2.setVisibility(View.INVISIBLE);
        }
        QueueManager.getInstance().handleSaveQueueAndPlaySong(MainActivity.this, arrayList, 0);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleOpenFromNotification(intent);
    }
}