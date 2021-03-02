package com.example.animemusic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.utils.SharedPrefHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SongOptionsDialog extends BottomSheetDialogFragment {
    private OnActionListener listener;
    private OnRemoveFromRecentListener onRemoveFromRecentListener;
    private String title;

    public interface OnActionListener {
        void onAddTo();

        void onAddToFavorite();

        void onShare();
    }

    public interface OnRemoveFromRecentListener {
        void onClick();
    }

    public SongOptionsDialog(String str) {
        this.title = str;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.listener = onActionListener;
    }

    public void setOnRemoveFromRecentListener(OnRemoveFromRecentListener onRemoveFromRecentListener2) {
        this.onRemoveFromRecentListener = onRemoveFromRecentListener2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_song_options, viewGroup, false);
        TextView textView = inflate.findViewById(R.id.title);
        String str = this.title;
        if (str != null) {
            textView.setText(str);
        }
        inflate.findViewById(R.id.favorite).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AppStats appStats ;
                if (!(getActivity() == null || (appStats = SharedPrefHelper.getInstance(getActivity()).getAppStats()) == null)){
                    appStats.setNumberOfFavorite(appStats.getNumberOfFavorite() + 1);
                    appStats.setAddSongToLocalCounter(appStats.getAddSongToLocalCounter() + 1);
                    SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);

                    if (getActivity() instanceof MainActivity){
                        if (appStats.getAddSongToLocalCounter() >= 5) {
                            appStats.setAddSongToLocalCounter(0);
                            SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);
                        }
                    }
                }
                listener.onAddToFavorite();
            }
        });
        inflate.findViewById(R.id.add_to_playlist).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                listener.onAddTo();
            }
        });

        inflate.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AppStats appStats ;
                if (!(getActivity() == null || (appStats = SharedPrefHelper.getInstance(getActivity()).getAppStats()) == null)){
                    appStats.setNumberOfFavorite(appStats.getNumberOfFavorite() + 1);
                    appStats.setAddSongToLocalCounter(appStats.getAddSongToLocalCounter() + 1);
                    SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);

                    if (getActivity() instanceof MainActivity){
                        if (appStats.getAddSongToLocalCounter() >= 10) {
                            appStats.setAddSongToLocalCounter(0);
                            SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);
                        }
                    }
                }
                listener.onShare();
            }
        });

        return inflate;
    }

}
