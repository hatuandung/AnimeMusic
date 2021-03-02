package com.example.animemusic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.animemusic.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LocalPlaylistOptionsDialog extends BottomSheetDialogFragment {
    private OnActionListener listener;
    private String title;

    public interface OnActionListener {
        void onRemove();

        void onRename();
    }

    public LocalPlaylistOptionsDialog(String str) {
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
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_my_playlist_options, viewGroup, false);
        TextView textView = (TextView) inflate.findViewById(R.id.title);
        String str = this.title;
        if (str != null) {
            textView.setText(str);
        }

        inflate.findViewById(R.id.remove_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnActionListener onActionListener = listener;
                if (onActionListener != null) {
                    onActionListener.onRemove();
                }
            }
        });

        inflate.findViewById(R.id.change_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnActionListener onActionListener = listener;
                if (onActionListener != null) {
                    onActionListener.onRename();
                }
            }
        });

        return inflate;
    }

}
