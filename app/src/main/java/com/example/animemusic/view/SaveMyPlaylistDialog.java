package com.example.animemusic.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.animemusic.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



public class SaveMyPlaylistDialog extends BottomSheetDialogFragment {
    private OnActionListener listener;
    private String placeholder;


    public interface OnActionListener {
        void onCancel();

        void onOk(String str);
    }

    public SaveMyPlaylistDialog() {
    }

    public SaveMyPlaylistDialog(String str) {
        this.placeholder = str;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.listener = onActionListener;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_add_playlist, viewGroup, false);
        EditText editText = (EditText) inflate.findViewById(R.id.name_input);
        String str = this.placeholder;
        if (str != null) {
            editText.setText(str);
        } else {
            editText.setText("My Playlist");
        }
        editText.requestFocus();
        editText.selectAll();
        ((Button) inflate.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                listener.onCancel();
            }
        });
        final Button button = (Button) inflate.findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOk(editText.getText().toString());
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().equals("")) {
                    button.setEnabled(false);
                    button.setBackground(SaveMyPlaylistDialog.this.getResources().getDrawable(R.drawable.bg_round_btn));
                    button.setTextColor(SaveMyPlaylistDialog.this.getResources().getColor(R.color.text_disabled));
                    return;
                }
                button.setEnabled(true);
                button.setBackground(SaveMyPlaylistDialog.this.getResources().getDrawable(R.drawable.bg_round_btn_accent));
                button.setTextColor(SaveMyPlaylistDialog.this.getResources().getColor(R.color.text_title));
            }
        });
        return inflate;
    }

}
