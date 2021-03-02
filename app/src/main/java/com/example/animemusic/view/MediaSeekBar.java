package com.example.animemusic.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.animemusic.utils.Helper;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;


public class MediaSeekBar extends AppCompatSeekBar {
    /* access modifiers changed from: private */
    public TextView durationView;
    private ControllerCallback mControllerCallback;
    /* access modifiers changed from: private */
    public boolean mIsTracking = false;
    /* access modifiers changed from: private */
    public MediaControllerCompat mMediaController;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    /* access modifiers changed from: private */
    public ValueAnimator mProgressAnimator;
    /* access modifiers changed from: private */
    public TextView positionTimeView;
    public SeekBar seekBar;

    public MediaSeekBar(Context context) {
        super(context);
        OnSeekBarChangeListener mediaSeekBar = new OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MediaSeekBar.this.positionTimeView != null) {
                    MediaSeekBar.this.positionTimeView.setText(Helper.durationToString(progress));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                boolean unused = MediaSeekBar.this.mIsTracking = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MediaSeekBar.this.mMediaController != null) {
                    MediaSeekBar.this.mMediaController.getTransportControls().seekTo((long) MediaSeekBar.this.getProgress());
                }
                boolean unused = MediaSeekBar.this.mIsTracking = false;

            }
        };

        this.mOnSeekBarChangeListener = mediaSeekBar;
        super.setOnSeekBarChangeListener(mediaSeekBar);
    }

    public MediaSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        OnSeekBarChangeListener r1 = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (MediaSeekBar.this.positionTimeView != null) {
                    MediaSeekBar.this.positionTimeView.setText(Helper.durationToString(i));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                boolean unused = MediaSeekBar.this.mIsTracking = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MediaSeekBar.this.mMediaController != null) {
                    MediaSeekBar.this.mMediaController.getTransportControls().seekTo((long) MediaSeekBar.this.getProgress());
                }
                boolean unused = MediaSeekBar.this.mIsTracking = false;
            }
        };
        this.mOnSeekBarChangeListener = r1;
        super.setOnSeekBarChangeListener(r1);
    }

    public MediaSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        OnSeekBarChangeListener r1 = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (MediaSeekBar.this.positionTimeView != null) {
                    MediaSeekBar.this.positionTimeView.setText(Helper.durationToString(i));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                boolean unused = MediaSeekBar.this.mIsTracking = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MediaSeekBar.this.mMediaController != null) {
                    MediaSeekBar.this.mMediaController.getTransportControls().seekTo((long) MediaSeekBar.this.getProgress());
                }
                boolean unused = MediaSeekBar.this.mIsTracking = false;
            }
        };
        this.mOnSeekBarChangeListener = r1;
        super.setOnSeekBarChangeListener(r1);
    }

    public final void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        throw new UnsupportedOperationException("Cannot add listeners to a MediaSeekBar");
    }

    public void setMediaController(MediaControllerCompat mediaControllerCompat) {
        if (mediaControllerCompat != null) {
            ControllerCallback controllerCallback = new ControllerCallback();
            this.mControllerCallback = controllerCallback;
            mediaControllerCompat.registerCallback(controllerCallback);
        } else {
            MediaControllerCompat mediaControllerCompat2 = this.mMediaController;
            if (mediaControllerCompat2 != null) {
                mediaControllerCompat2.unregisterCallback(this.mControllerCallback);
                this.mControllerCallback = null;
            }
        }
        this.mMediaController = mediaControllerCompat;
    }

    public void setTimeView(TextView textView, TextView textView2) {
        this.positionTimeView = textView;
        this.durationView = textView2;
    }

    public void disconnectController() {
        MediaControllerCompat mediaControllerCompat = mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mControllerCallback);
            ValueAnimator valueAnimator = this.mProgressAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                mProgressAnimator = null;
            }
            mControllerCallback = null;
            mMediaController = null;
        }
    }

    private class ControllerCallback extends MediaControllerCompat.Callback implements ValueAnimator.AnimatorUpdateListener {
        private ControllerCallback() {
        }

        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            super.onPlaybackStateChanged(playbackStateCompat);
            if (MediaSeekBar.this.mProgressAnimator != null) {
                MediaSeekBar.this.mProgressAnimator.cancel();
                ValueAnimator unused = MediaSeekBar.this.mProgressAnimator = null;
            }
            int position = playbackStateCompat != null ? (int) playbackStateCompat.getPosition() : 0;
            MediaSeekBar.this.setProgress(position);
            if (MediaSeekBar.this.positionTimeView != null) {
                MediaSeekBar.this.positionTimeView.setText(Helper.durationToString(position));
            }
            if (playbackStateCompat != null && playbackStateCompat.getState() == STATE_PLAYING) {
                int max = Math.max((int) (((float) (MediaSeekBar.this.getMax() - position)) / playbackStateCompat.getPlaybackSpeed()), 0);
                MediaSeekBar mediaSeekBar = MediaSeekBar.this;
                ValueAnimator unused2 = mediaSeekBar.mProgressAnimator = ValueAnimator.ofInt(new int[]{position, mediaSeekBar.getMax()}).setDuration((long) max);
                MediaSeekBar.this.mProgressAnimator.setInterpolator(new LinearInterpolator());
                MediaSeekBar.this.mProgressAnimator.addUpdateListener(this);
                MediaSeekBar.this.mProgressAnimator.start();
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            super.onMetadataChanged(mediaMetadataCompat);
            int i = mediaMetadataCompat != null ? (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) : 0;
            MediaSeekBar.this.setProgress(0);
            MediaSeekBar.this.setMax(i);
            if (MediaSeekBar.this.mMediaController != null) {
                onPlaybackStateChanged(MediaSeekBar.this.mMediaController.getPlaybackState());
            }
            if (MediaSeekBar.this.durationView != null) {
                MediaSeekBar.this.durationView.setText(Helper.durationToString(i));
            }
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (MediaSeekBar.this.mIsTracking) {
                valueAnimator.cancel();
                return;
            }
            int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            MediaSeekBar.this.setProgress(intValue);
            if (MediaSeekBar.this.positionTimeView != null) {
                MediaSeekBar.this.positionTimeView.setText(Helper.durationToString(intValue));
            }
        }
    }
}