package com.example.animemusic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.animemusic.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ConfirmRemoveMyPlaylistDialog extends BottomSheetDialogFragment {
    private OnActionListener listener;

    public interface OnActionListener {
        void onCancel();

        void onOk();
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.listener = onActionListener;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_confirm_remove_my_playlist, viewGroup, false);

        inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
            }
        });

        inflate.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOk();
            }
        });

        return inflate;
    }

}
