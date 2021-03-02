package com.example.animemusic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.animemusic.R;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.utils.SharedPrefHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LocalSongOptionsDialog extends BottomSheetDialogFragment {
    private OnActionListener listener;
    private String title;

    public interface OnActionListener {
        void onRemove();

        void onShare();
    }

    public LocalSongOptionsDialog(String str) {
        this.title = str;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.listener = onActionListener;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_local_song_options, viewGroup, false);
        TextView textView = inflate.findViewById(R.id.title);
        String str = this.title;
        if (str != null) {
            textView.setText(str);
        }
        inflate.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                listener.onRemove();
            }
        });
        inflate.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AppStats appStats;
                if (!(getActivity() == null || (appStats = SharedPrefHelper.getInstance(getActivity()).getAppStats()) == null)) {
                    appStats.setNumberOfSongShare(appStats.getNumberOfSongShare() + 1);
                    appStats.setSharedSongCounter(appStats.getSharedSongCounter() + 1);
                    SharedPrefHelper.getInstance(getActivity()).saveAppStats(appStats);
                }
                listener.onShare();
            }
        });
        return inflate;
    }


}
