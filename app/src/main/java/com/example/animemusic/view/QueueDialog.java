package com.example.animemusic.view;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.adapter.ItemTouchHelperCallback;
import com.example.animemusic.adapter.SongQueueAdapter;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;


public class QueueDialog extends DialogFragment {

    public TextView currentSongDesc;

    public ImageView currentSongThumb;

    public TextView currentSongTitle;
    //private SpinKitView expandLoading;
    private ImageView expandPlay;

    public boolean isPlaying = false;
    private ControllerCallback mControllerCallback;
    private LinearLayoutManager mLayoutManager;
    private MediaControllerCompat mMediaController;

    public List<Song> queue;

    public SongQueueAdapter songAdapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_queue, viewGroup, false);
        this.currentSongThumb = (ImageView) inflate.findViewById(R.id.current_song_thumb);
        this.currentSongTitle = (TextView) inflate.findViewById(R.id.current_song_title);
        this.currentSongDesc = (TextView) inflate.findViewById(R.id.current_song_desc);

        ((ImageView) inflate.findViewById(R.id.close_btn)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dismiss();
            }
        });

        setUpControlMedia(inflate);
        //queue.clear();
        queue = SharedPrefHelper.getInstance(getContext()).getQueue();
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.current_queue);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(inflate.getContext());
        mLayoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(linearLayoutManager);
        SongQueueAdapter songQueueAdapter = new SongQueueAdapter(getContext());
        songAdapter = songQueueAdapter;
        recyclerView.setAdapter(songQueueAdapter);

        songAdapter.setSongs((ArrayList<Song>) queue);

        scrollToCurrentPlay();


        songAdapter.setListener(new SongQueueAdapter.SongQueueItemListener() {
            @Override
            public void onQueueClickedListener(int position) {
                QueueManager.getInstance().handleSaveQueueAndPlaySong(getActivity(), queue, position);
            }
        });

        addItemTouchCallback(recyclerView);
        return inflate;
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

    private class ControllerCallback extends MediaControllerCompat.Callback {
        private ControllerCallback() {
        }

        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            super.onPlaybackStateChanged(playbackStateCompat);
            boolean unused = QueueDialog.this.isPlaying = playbackStateCompat != null && playbackStateCompat.getState() == STATE_PLAYING;
            if (playbackStateCompat != null) {
                QueueDialog.this.setUpControlViewState(playbackStateCompat);
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            super.onMetadataChanged(mediaMetadataCompat);
            MediaDescriptionCompat description = mediaMetadataCompat.getDescription();
            QueueDialog.this.currentSongTitle.setText(description.getTitle());
            QueueDialog.this.currentSongDesc.setText(description.getDescription());

            Picasso.get().load(description.getIconUri()).placeholder((int) R.drawable.placeholder_song).into(QueueDialog.this.currentSongThumb);

            QueueDialog.this.songAdapter.notifyDataSetChanged();
            QueueDialog.this.scrollToCurrentPlay();
        }
    }

    /* access modifiers changed from: private */
    public void scrollToCurrentPlay() {
        int queueIndex = SharedPrefHelper.getInstance(getContext()).getQueueIndex(-1);
        int findLastVisibleItemPosition = this.mLayoutManager.findLastVisibleItemPosition();
        int findFirstVisibleItemPosition = this.mLayoutManager.findFirstVisibleItemPosition();
        if ((queueIndex <= findLastVisibleItemPosition && queueIndex >= findFirstVisibleItemPosition) || getContext() == null || findFirstVisibleItemPosition - 1 == queueIndex) {
            this.mLayoutManager.scrollToPosition(queueIndex);
            return;
        }

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()){
            @Override
            protected int getVerticalSnapPreference() {
                return -1;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 30.0f / ((float) displayMetrics.densityDpi);
            }
        };

        if (queueIndex > 10) {
            this.mLayoutManager.scrollToPosition(queueIndex - 10);
        }
        smoothScroller.setTargetPosition(queueIndex);
        this.mLayoutManager.startSmoothScroll(smoothScroller);
    }

    private void setUpControlMedia(View view) {
        this.expandPlay = (ImageView) view.findViewById(R.id.expand_play);
        ImageView imageView = (ImageView) view.findViewById(R.id.expand_next);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.expand_prev);
//        this.expandLoading = (SpinKitView) view.findViewById(R.id.expand_loading);
//        this.expandLoading.setIndeterminateDrawable((Sprite) new DoubleBounce());
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (!(mediaControllerCompat == null || mediaControllerCompat.getPlaybackState() == null)) {
            this.isPlaying = this.mMediaController.getPlaybackState() != null && this.mMediaController.getPlaybackState().getState() == STATE_PLAYING;
            setUpControlViewState(this.mMediaController.getPlaybackState());
        }
        this.expandPlay.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QueueDialog.this.lambda$setUpControlMedia$2$QueueDialog(view);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QueueDialog.this.lambda$setUpControlMedia$3$QueueDialog(view);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QueueDialog.this.lambda$setUpControlMedia$4$QueueDialog(view);
            }
        });
    }

    public /* synthetic */ void lambda$setUpControlMedia$2$QueueDialog(View view) {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            if (this.isPlaying) {
                mediaControllerCompat.getTransportControls().pause();
            } else {
                mediaControllerCompat.getTransportControls().play();
            }
        }
    }

    public /* synthetic */ void lambda$setUpControlMedia$3$QueueDialog(View view) {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.getTransportControls().skipToNext();
        }
    }

    public /* synthetic */ void lambda$setUpControlMedia$4$QueueDialog(View view) {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.getTransportControls().skipToPrevious();
        }
    }

    /* access modifiers changed from: private */
    public void setUpControlViewState(PlaybackStateCompat playbackStateCompat) {
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            this.expandPlay.animate().alpha(0.0f);
//            this.expandLoading.setVisibility(0);
            return;
        }
        this.expandPlay.animate().alpha(1.0f);
//        this.expandLoading.setVisibility(4);
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
            this.expandPlay.setImageResource(R.drawable.ic_expand_play);
        } else if (playbackStateCompat.getState() == STATE_PLAYING) {
            this.expandPlay.setImageResource(R.drawable.ic_expand_pause);
        }
    }

    private void disconnectController() {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(this.mControllerCallback);
            this.mControllerCallback = null;
            this.mMediaController = null;
        }
    }

    public void onStop() {
        super.onStop();
        disconnectController();
    }

    private void addItemTouchCallback(RecyclerView recyclerView) {
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(new ItemTouchHelperCallback.ItemTouchListener() {
            public void onMove(int i, int i2) {
                songAdapter.onMove(i, i2);
            }
        }));
        songAdapter.setStartDragListener(new SongQueueAdapter.StartDragListener() {
            public void requestDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            public void postDrag(List<Song> list, int i) {
                List unused = QueueDialog.this.queue = list;
                QueueManager.getInstance().handleSaveQueue(QueueDialog.this.getContext(), list, i);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
        }
    }
}
