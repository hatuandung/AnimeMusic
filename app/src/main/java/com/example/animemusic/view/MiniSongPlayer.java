package com.example.animemusic.view;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animemusic.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class MiniSongPlayer extends RelativeLayout {
    private boolean isPlaying = false;
    private ControllerCallback mControllerCallback;
    private MediaControllerCompat mMediaController;
    private RoundedImageView miniArt;
    private TextView miniDescription;
    private SpinKitView miniLoading;
    private ImageView miniPlay;
    private MediaSeekBar miniSeekBar;
    private TextView miniTitle;

    public MiniSongPlayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        View inflate = inflate(context, R.layout.include_player_mini, this);
        miniPlay = inflate.findViewById(R.id.mini_play);
        miniLoading = inflate.findViewById(R.id.mini_loading);
        miniSeekBar = inflate.findViewById(R.id.seek_bar);

        miniPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null || mediaControllerCompat.getTransportControls() == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else if (isPlaying) {
                    mMediaController.getTransportControls().pause();
                } else {
                    mMediaController.getTransportControls().play();
                }
            }
        });

        inflate.findViewById(R.id.mini_next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat mediaControllerCompat = mMediaController;
                if (mediaControllerCompat == null) {
                    return;
                }
                if (mediaControllerCompat.getTransportControls() == null) {
                    Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    mMediaController.getTransportControls().skipToNext();
                }
            }
        });


        inflate.findViewById(R.id.mini_previous).setOnClickListener(new OnClickListener() {
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

        miniTitle =  inflate.findViewById(R.id.mini_title);
        miniDescription = inflate.findViewById(R.id.mini_description);
        miniArt = findViewById(R.id.mini_art);
        miniLoading.setIndeterminateDrawable((Sprite) new Circle());
    }

    public void setMediaController(MediaControllerCompat mediaControllerCompat) {
        if (mediaControllerCompat != null) {
            ControllerCallback controllerCallback = new ControllerCallback();
            this.mControllerCallback = controllerCallback;
            mediaControllerCompat.registerCallback(controllerCallback);
        } else {
            MediaControllerCompat mediaControllerCompat2 = mMediaController;
            if (mediaControllerCompat2 != null) {
                mediaControllerCompat2.unregisterCallback(mControllerCallback);
                mControllerCallback = null;
            }
        }
        mMediaController = mediaControllerCompat;
        miniSeekBar.setMediaController(mediaControllerCompat);
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
                updateUIByPlaybackState(playbackStateCompat);
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            super.onMetadataChanged(mediaMetadataCompat);
            MiniSongPlayer miniSongPlayer = MiniSongPlayer.this;
            isPlaying = miniSongPlayer.mMediaController.getPlaybackState() != null && mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
            if (mediaMetadataCompat != null) {
                if (mMediaController.getPlaybackState() != null) {
                    MiniSongPlayer miniSongPlayer2 = MiniSongPlayer.this;
                    miniSongPlayer2.updateUIByPlaybackState(miniSongPlayer2.mMediaController.getPlaybackState());
                }
                handleSongChangedForMiniPlayer(mediaMetadataCompat.getDescription(), mediaMetadataCompat);
            }
        }
    }

    public void updateUIByPlaybackState(PlaybackStateCompat playbackStateCompat) {
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            miniPlay.setVisibility(INVISIBLE);
            miniLoading.setVisibility(VISIBLE);
            return;
        }
        miniPlay.setVisibility(VISIBLE);
        miniLoading.setVisibility(INVISIBLE);
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
            miniPlay.setImageResource(R.drawable.ic_mini_play);
        } else if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            miniPlay.setImageResource(R.drawable.ic_mini_pause);
        }
    }

    public void handleSongChangedForMiniPlayer(MediaDescriptionCompat mediaDescriptionCompat, MediaMetadataCompat mediaMetadataCompat) {
        miniTitle.setText(mediaDescriptionCompat.getTitle());
        miniDescription.setText(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
        Picasso.get().load(mediaDescriptionCompat.getIconUri()).placeholder((int) R.drawable.placeholder_song).into((ImageView) miniArt);
    }

    public void disconnect() {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(this.mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
        miniSeekBar.disconnectController();
    }
}
