package com.example.animemusic.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.asyncTasks.GetPlaylistSong;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;
import com.example.animemusic.service.MusicLibrary;
import com.example.animemusic.utils.Color;
import com.example.animemusic.utils.Helper;
import com.example.animemusic.utils.SharedPrefHelper;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Date;


public class ExpandSongPlayer extends RelativeLayout {

    public Runnable alarmCallback = new Runnable() {
        public void run() {
            MediaControllerCompat.TransportControls transportControls = MediaControllerCompat.getMediaController((Activity) getContext()).getTransportControls();
            if (transportControls != null) {
                transportControls.pause();
                timeAlarmMinute = 0;
                timeStartAlarm = 0;
                isAlarmOn = false;
                btnAlarm.setImageResource(R.drawable.ic_add_alarm);
            }
        }
    };

    public Handler alarmHandler = new Handler();

    public ImageView btnAlarm;

    private RoundedImageView expandArt;
    private TextView expandDesc;
    private SpinKitView expandLoading;

    private ImageView expandPlay;
    private MediaSeekBar expandSeekBar;
    private RelativeLayout expandSongPlayer;
    private TextView expandTitle;
    private FavoriteButton favoriteBtn;
    public boolean isAlarmOn = false;
    public boolean isPlaying = false;
    private ControllerCallback mControllerCallback;
    public MediaControllerCompat mMediaController;
    MainActivity mainActivity;

    private ImageView shareSong;
    public int timeAlarmMinute = 0;
    public long timeStartAlarm = 0;

    public ExpandSongPlayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {

        mainActivity = (MainActivity) context;

        View inflate = inflate(context, R.layout.include_player_expand, this);

        expandSongPlayer = inflate.findViewById(R.id.expand_song_player);
        expandArt = inflate.findViewById(R.id.expand_art);
        expandTitle = inflate.findViewById(R.id.expand_title);
        expandDesc = inflate.findViewById(R.id.expand_desc);
        expandPlay = inflate.findViewById(R.id.expand_play);
        expandLoading = inflate.findViewById(R.id.expand_loading);
        RepeatButton repeatButton = inflate.findViewById(R.id.repeat);
        ShuffleButton shuffleButton = inflate.findViewById(R.id.shuffle);
        expandSeekBar = inflate.findViewById(R.id.expand_seek_bar);
        expandSeekBar.setTimeView(inflate.findViewById(R.id.position_time), inflate.findViewById(R.id.duration));
        favoriteBtn = inflate.findViewById(R.id.favorite);
        shareSong = inflate.findViewById(R.id.share_song);

        btnAlarm = inflate.findViewById(R.id.add_alarm);
        expandLoading.setIndeterminateDrawable((Sprite) new DoubleBounce());
        expandSongPlayer.setOnClickListener((View.OnClickListener) null);

        expandPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else if (isPlaying) {
                    mediaControllerCompat.getTransportControls().pause();
                } else {
                    mediaControllerCompat.getTransportControls().play();
                }
            }
        });

        inflate.findViewById(R.id.expand_next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    mediaControllerCompat.getTransportControls().skipToNext();
                }
            }
        });

        inflate.findViewById(R.id.expand_prev).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    return;
                }
                if (mediaControllerCompat.getTransportControls() == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    mMediaController.getTransportControls().skipToPrevious();
                }
            }
        });


        repeatButton.setState(SharedPrefHelper.getInstance(context).getRepeatMode(2));


        repeatButton.setListener(new RepeatButton.ChangeStateListener() {
            @Override
            public void onChangeState(int i) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    return;
                }
                if (mediaControllerCompat.getTransportControls() == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    mMediaController.getTransportControls().setRepeatMode(repeatButton.getState());
                }
            }
        });

        shuffleButton.setState(SharedPrefHelper.getInstance(context).getShuffleMode(0));

        shuffleButton.setListener(new ShuffleButton.ChangeStateListener() {
            @Override
            public void onChangeState(int i) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    return;
                }
                if (mediaControllerCompat.getTransportControls() == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    mMediaController.getTransportControls().setShuffleMode(shuffleButton.getState());
                }
            }
        });


        inflate.findViewById(R.id.queue).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaController == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    return;
                }
                QueueDialog queueDialog = new QueueDialog();
                queueDialog.show(mainActivity.getSupportFragmentManager(), queueDialog.getTag());
                queueDialog.setMediaController(mMediaController);
            }
        });


        btnAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmTimerDialog alarmTimerDialog = new AlarmTimerDialog(context);
                if (isAlarmOn) {
                    alarmTimerDialog.setAlarmMinute(timeAlarmMinute - ((int) ((new Date().getTime() - timeStartAlarm) / DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS)));
                }
                alarmTimerDialog.setListener(new AlarmTimerDialog.AlarmTimerListener() {
                    public void onCancel() {
                        ExpandSongPlayer.this.alarmHandler.removeCallbacksAndMessages((Object) null);
                        timeAlarmMinute = 0;
                        timeStartAlarm = 0;
                        isAlarmOn = false;
                        ExpandSongPlayer.this.btnAlarm.setImageResource(R.drawable.ic_add_alarm);
                    }

                    public void onOk(int i) {
                        ExpandSongPlayer.this.alarmHandler.removeCallbacksAndMessages((Object) null);
                        ExpandSongPlayer.this.alarmHandler.postDelayed(ExpandSongPlayer.this.alarmCallback, (long) (i * 60 * 1000));
                        timeAlarmMinute = i;
                        timeStartAlarm = new Date().getTime();
                        isAlarmOn = true;
                        ExpandSongPlayer.this.btnAlarm.setImageResource(R.drawable.ic_alarm_on);
                    }
                });
                alarmTimerDialog.show();
            }
        });


    }

    public void setMediaController(MediaControllerCompat mediaControllerCompat) {
        if (mediaControllerCompat != null) {
            ControllerCallback controllerCallback = new ControllerCallback();
            mControllerCallback = controllerCallback;
            mediaControllerCompat.registerCallback(controllerCallback);
        } else {
            MediaControllerCompat mediaControllerCompat2 = mMediaController;
            if (mediaControllerCompat2 != null) {
                mediaControllerCompat2.unregisterCallback(mControllerCallback);
                mControllerCallback = null;
            }
        }
        mMediaController = mediaControllerCompat;
        expandSeekBar.setMediaController(mediaControllerCompat);
    }

    private class ControllerCallback extends MediaControllerCompat.Callback {
        private ControllerCallback() {
        }

        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            super.onPlaybackStateChanged(playbackStateCompat);
            isPlaying = playbackStateCompat != null && playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING;
            if (playbackStateCompat != null) {
                ExpandSongPlayer.this.updateUIByPlaybackState(playbackStateCompat);
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            super.onMetadataChanged(mediaMetadataCompat);
            if (mediaMetadataCompat != null && mMediaController != null) {
                ExpandSongPlayer expandSongPlayer = ExpandSongPlayer.this;
                isPlaying = expandSongPlayer.mMediaController.getPlaybackState() != null && mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
                if (mMediaController.getPlaybackState() != null) {
                    ExpandSongPlayer expandSongPlayer2 = ExpandSongPlayer.this;
                    expandSongPlayer2.updateUIByPlaybackState(expandSongPlayer2.mMediaController.getPlaybackState());
                }
                handleExpandSongPlayer(mediaMetadataCompat.getDescription(), mediaMetadataCompat);
            }
        }
    }

    public void updateUIByPlaybackState(PlaybackStateCompat playbackStateCompat) {
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            expandPlay.animate().alpha(0.0f);
            expandLoading.setVisibility(VISIBLE);
            return;
        }
        expandPlay.animate().alpha(1.0f);
        expandLoading.setVisibility(INVISIBLE);
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
            expandPlay.setImageResource(R.drawable.ic_expand_play);
        } else if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            expandPlay.setImageResource(R.drawable.ic_expand_pause);
        }
    }


    public void handleExpandSongPlayer(MediaDescriptionCompat mediaDescriptionCompat, MediaMetadataCompat mediaMetadataCompat) {
        expandTitle.setText(mediaDescriptionCompat.getTitle());
        Picasso.get().load(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)).placeholder((int) R.drawable.placeholder_song).into((ImageView) this.expandArt);
        expandDesc.setText(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
        PlaylistSong playlistSong = new PlaylistSong(mediaDescriptionCompat.getMediaId(), 1);
        new GetPlaylistSong((Activity) getContext(), playlistSong, new GetPlaylistSong.GetPlaylistSongListener() {
            public final void onComplete(PlaylistSong playlistSong) {
                if (playlistSong == null) {
                    favoriteBtn.setState(false);
                } else {
                    favoriteBtn.setState(true);
                }
            }
        }).execute();
        Song song = new Song();
        song.setId(mediaDescriptionCompat.getMediaId());
        song.setStreamUrl(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        song.setTitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        song.setDescription(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
        song.setArtworkUrl(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ART_URI));
        song.setStreamType(mediaMetadataCompat.getString(MusicLibrary.METADATA_KEY_MEDIA_STREAM_TYPE));
        song.setMediaType(mediaMetadataCompat.getString(MusicLibrary.METADATA_KEY_MEDIA_TYPE));
        song.setDuration((int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));


        favoriteBtn.setListener(new FavoriteButton.ChangeStateListener() {
            @Override
            public void onChangeState(boolean z) {
                if (!z) {
                    AppDatabase.getInstance(getContext()).getPlaylistSongDao().delete(playlistSong);
                    MainActivity mainActivity = (MainActivity) getContext();
                    mainActivity.getFmProfile().getFmTabFavorite().getData();
                    Toast.makeText(getContext(), getResources().getString(R.string.remove_from_favorite), Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    AppDatabase.getInstance(getContext()).getSongDao().insert(song);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    AppDatabase.getInstance(getContext()).getPlaylistSongDao().insert(playlistSong);
                    Log.e("onChangeState: ", playlistSong.songId);
                    MainActivity mainActivity = (MainActivity) getContext();
                    mainActivity.getFmProfile().getFmTabFavorite().getData();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Toast.makeText(getContext(), getResources().getString(R.string.added_to_favorite), Toast.LENGTH_LONG).show();
                AppStats appStats = SharedPrefHelper.getInstance(getContext()).getAppStats();
            }
        });

        int dimension = (int) getResources().getDimension(R.dimen.image_size);
        if (mediaMetadataCompat.getDescription().getIconUri() != null) {
            Color.loadBgBannerAverage(dimension, this.expandSongPlayer, mediaMetadataCompat.getDescription().getIconUri().toString());
        }

        shareSong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.shareSong(song, new WeakReference(mainActivity));
                AppStats appStats = SharedPrefHelper.getInstance(getContext()).getAppStats();
            }
        });
    }

    public void disconnect() {
        MediaControllerCompat mediaControllerCompat = mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
        expandSeekBar.disconnectController();

    }
}
