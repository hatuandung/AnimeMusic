package com.example.animemusic.service;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;


public final class MediaPlayerAdapter extends PlayerAdapter {
    private final Context mContext;
    private MediaMetadataCompat mCurrentMedia;
    private boolean mCurrentMediaPlayedToCompletion;
    private String mFilename;
    private MediaPlayer mMediaPlayer;

    public PlaybackInfoListener mPlaybackInfoListener;
    private int mSeekWhileNotPlaying = -1;
    private int mState;

    public void playFromUri(MediaMetadataCompat mediaMetadataCompat) {
    }

    public MediaPlayerAdapter(Context context, PlaybackInfoListener playbackInfoListener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = playbackInfoListener;
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mMediaPlayer = mediaPlayer;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPlaybackInfoListener.onPlaybackCompleted();
                    setNewState(2);
                }
            });
        }
    }

    public void playFromMedia(MediaMetadataCompat mediaMetadataCompat) {
        mCurrentMedia = mediaMetadataCompat;
        mediaMetadataCompat.getDescription().getMediaId();
    }

    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    private void playFile(String str) {
        String str2 = mFilename;
        boolean z = true;
        boolean z2 = str2 == null || !str.equals(str2);
        if (mCurrentMediaPlayedToCompletion) {
            mCurrentMediaPlayedToCompletion = false;
        } else {
            z = z2;
        }
        if (z) {
            release();
            mFilename = str;
            initializeMediaPlayer();
            try {
                AssetFileDescriptor openFd = mContext.getAssets().openFd(mFilename);
                mMediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
                try {
                    mMediaPlayer.prepare();
                    play();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to open file: " + mFilename, e);
                }
            } catch (Exception e2) {
                throw new RuntimeException("Failed to open file: " + mFilename, e2);
            }
        } else if (!isPlaying()) {
            play();
        }
    }

    public void onStop() {
        setNewState(1);
        release();
    }

    private void release() {
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        MediaPlayer mediaPlayer = mMediaPlayer;
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void onPlay() {
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setNewState(3);
        }
    }

    public void onPause() {
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setNewState(2);
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
            MediaPlayer mediaPlayer = mMediaPlayer;
            currentPosition = mediaPlayer == null ? 0 : (long) mediaPlayer.getCurrentPosition();
        }
        long j = currentPosition;
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(getAvailableActions());
        builder.setState(mState, j, 1.0f, SystemClock.elapsedRealtime());
        mPlaybackInfoListener.onPlaybackStateChange(builder.build());
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
//        int i = mState;
//        if (i == 1) {
//            return 3126;
//        }
//        if (i != 2) {
//            return i != 3 ? 3639 : 3379;
//        }
//        return 3125;

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
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mSeekWhileNotPlaying = (int) j;
            }
            mMediaPlayer.seekTo((int) j);
            setNewState(mState);
        }
    }

    public void setVolume(float f) {
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(f, f);
        }
    }
}
