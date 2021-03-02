package com.example.animemusic.service;


import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.animemusic.R;
import com.example.animemusic.api.SoundCloundBuilder;
import com.example.animemusic.asyncTasks.GeneratePresignedS3;
import com.example.animemusic.interfaces.MediaType;
import com.example.animemusic.interfaces.StreamType;
import com.example.animemusic.models.StreamResp;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public final class ExoPlayerAdapter extends PlayerAdapter {
    private boolean isS3Streaming;
    private final Context mContext;

    public MediaMetadataCompat mCurrentMedia;

    public boolean mCurrentMediaPlayedToCompletion;

    public SimpleExoPlayer mExoPlayer;
    private String mMediaId;

    public PlaybackInfoListener mPlaybackInfoListener;
    private int mSeekWhileNotPlaying = -1;
    private int mState;

    public long resumePosition = -1;

    public boolean retry403 = false;
    private String scClientId;

    public void playFromMedia(MediaMetadataCompat mediaMetadataCompat) {
    }

    public ExoPlayerAdapter(Context context, PlaybackInfoListener playbackInfoListener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = playbackInfoListener;
    }

    public ExoPlayerAdapter(Context context, PlaybackInfoListener playbackInfoListener, String str, boolean z) {
        super(context);
        this.mContext = context.getApplicationContext();
        this.mPlaybackInfoListener = playbackInfoListener;
        this.scClientId = str;
        this.isS3Streaming = z;
    }

    private void initializeMediaPlayer() {
        if (mExoPlayer == null) {
            SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
            mExoPlayer = newSimpleInstance;

            newSimpleInstance.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == PlaybackStateCompat.STATE_FAST_FORWARDING) {
                        mPlaybackInfoListener.onPlaybackCompleted();
                        mCurrentMediaPlayedToCompletion = true;
                    }
                    if (playWhenReady && playbackState == PlaybackStateCompat.STATE_PLAYING) {
                        setNewState(PlaybackStateCompat.STATE_PLAYING);
                        if (resumePosition != -1) {
                            mExoPlayer.seekTo(resumePosition);
                            resumePosition = -1;
                        }
                    }
                    if (playWhenReady && playbackState == PlaybackStateCompat.STATE_PAUSED) {
                        setNewState(PlaybackStateCompat.STATE_BUFFERING);
                    }

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    if (error.type == 0) {
                        retry403 = true;
                        ExoPlayerAdapter exoPlayerAdapter = ExoPlayerAdapter.this;
                        exoPlayerAdapter.playFromUri(exoPlayerAdapter.mCurrentMedia);
                    }

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }
    }

    public void playFromUri(MediaMetadataCompat mediaMetadataCompat) {
        if (mediaMetadataCompat == null) {
            Context context = this.mContext;
            Toast.makeText(context, context.getText(R.string.cannot_play), Toast.LENGTH_LONG).show();
            return;
        }

        mCurrentMedia = mediaMetadataCompat;
        Uri mediaUri = mediaMetadataCompat.getDescription().getMediaUri();
        Log.e("playFromUri: ", mediaUri.toString());

        final String mediaId = mediaMetadataCompat.getDescription().getMediaId();
        if (isS3Streaming) {
            playFromS3(mediaId);
        } else if (!mCurrentMedia.getString(MusicLibrary.METADATA_KEY_MEDIA_STREAM_TYPE).equals(StreamType.SOUNDCLOUD.getValue())) {
            streamFile(mediaUri, mediaId, MediaType.PROGRESSIVE);
        } else if (mediaUri == null) {
            Context context = this.mContext;
            Toast.makeText(context, context.getText(R.string.cannot_play), Toast.LENGTH_SHORT).show();
        } else {
            setNewState(PlaybackStateCompat.STATE_BUFFERING);
            SoundCloundBuilder.getInstance().getStreamUrl(mediaUri.toString(), scClientId).enqueue(new Callback<StreamResp>() {
                @Override
                public void onResponse(Call<StreamResp> call, Response<StreamResp> response) {
                    StreamResp streamResp = response.body();
                    try {
                        streamFile(Uri.parse(streamResp.getUrl()), mediaId, MediaType.HLS);
                    } catch (Exception ex) {
                        Toast.makeText(mContext, R.string.cannot_play, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<StreamResp> call, Throwable t) {
                    playFromS3(mediaId);
                }
            });
        }
    }

    public MediaMetadataCompat getCurrentMedia() {
        return this.mCurrentMedia;
    }

    public void playFromS3(final String str) {
        new GeneratePresignedS3(str, new GeneratePresignedS3.S3Listener() {
            public void onStart() {
                setNewState(PlaybackStateCompat.STATE_BUFFERING);
            }

            public void onEnd(URL url) {
                streamFile(Uri.parse(url.toString()), str, MediaType.PROGRESSIVE);
            }
        }).execute(new Context[]{mContext});
    }

    public void streamFile(Uri uri, String str, MediaType mediaType) {
        MediaSource mediaSource;
        boolean z = str == null || !str.equals(mMediaId);
        if (mCurrentMediaPlayedToCompletion) {
            mCurrentMediaPlayedToCompletion = false;
            z = true;
        }
        try {
            if (retry403) {
                retry403 = false;
                if (mExoPlayer == null) {
                    resumePosition = 0;
                } else {
                    resumePosition = mExoPlayer.getCurrentPosition();
                }
                z = true;
            }
            if (z) {
                release();
                mMediaId = str;
                initializeMediaPlayer();
                DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, this.mContext.getString(R.string.app_name)));
                if (mediaType == MediaType.HLS) {
                    mediaSource = new HlsMediaSource.Factory((DataSource.Factory) defaultDataSourceFactory).createMediaSource(uri);
                } else {
                    mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
                }
                this.mExoPlayer.prepare(mediaSource);
                play();
            } else if (!isPlaying()) {
                play();
            }
        } catch (Exception e) {
            Context context = mContext;
            Toast.makeText(context, context.getText(R.string.cannot_play), Toast.LENGTH_LONG).show();
            throw new RuntimeException("Failed to open file id: " + this.mMediaId, e);
        }
    }

    public void onStop() {
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void release() {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            this.mExoPlayer = null;
        }
    }

    public boolean isPlaying() {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        return simpleExoPlayer != null && simpleExoPlayer.getPlayWhenReady();
    }

    public void onPlay() {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        if (simpleExoPlayer != null && !simpleExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    public void onPause() {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        if (simpleExoPlayer != null && simpleExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(false);
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    public void setNewState(int i) {
        long currentPosition;
        mState = i;
        if (i == 1) {
            mCurrentMediaPlayedToCompletion = true;
        }
        int i2 = mSeekWhileNotPlaying;
        if (i2 >= 0) {
            currentPosition = (long) i2;
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        } else {
            SimpleExoPlayer simpleExoPlayer = mExoPlayer;
            currentPosition = simpleExoPlayer == null ? 0 : simpleExoPlayer.getCurrentPosition();
        }
        long j = currentPosition;
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(getAvailableActions());
        builder.setState(mState, j, 1.0f, SystemClock.elapsedRealtime());
        mPlaybackInfoListener.onPlaybackStateChange(builder.build());
        builder.addCustomAction(MusicService.CUSTOM_ACTION_STOP_AND_PLAY, MusicService.CUSTOM_ACTION_STOP_AND_PLAY, R.drawable.logo);
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    public void seekTo(long j) {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        if (simpleExoPlayer != null) {
            if (!simpleExoPlayer.getPlayWhenReady()) {
                mSeekWhileNotPlaying = (int) j;
            }
            mExoPlayer.seekTo((long) ((int) j));
        }
    }

    public void setVolume(float f) {
        SimpleExoPlayer simpleExoPlayer = mExoPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(f);
        }
    }
}
