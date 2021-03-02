package com.example.animemusic.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animemusic.R;
import com.github.ybq.android.spinkit.SpinKitView;


public class RequestSongDialog extends Dialog {

    public final Context context;

    public interface DialogListener {
        void onOk();
    }

    public RequestSongDialog(Context context2) {
        super(context2);
        this.context = context2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_request_song);
        TextView textView = findViewById(R.id.btn_submit);
        final EditText editText =findViewById(R.id.song_title);
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dismiss();
            }
        });
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(editText, 0);
                }
            }
        }, 0);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


//    public /* synthetic */ void lambda$onCreate$1$RequestSongDialog(EditText editText, final TextView textView, final SpinKitView spinKitView, View view) {
//        String obj = editText.getText().toString();
//        if (!obj.trim().equals("")) {
//            User sessionUser = SharedPrefHelper.getInstance(this.context).getSessionUser();
//            String apiToken = sessionUser != null ? sessionUser.getApiToken() : null;
//            textView.setClickable(false);
//            spinKitView.setVisibility(0);
//            RequestService.getInstance((String) null).requestSong(apiToken, obj.trim(), new APICallback<Object>() {
//                public void onSuccess(Object obj) {
//                    Toast.makeText(RequestSongDialog.this.context, RequestSongDialog.this.context.getText(R.string.dialog_request_song_success), 0).show();
//                    spinKitView.setVisibility(8);
//                    textView.setClickable(true);
//                    RequestSongDialog.this.dismiss();
//                }
//
//                public void onError(NetworkError networkError) {
//                    spinKitView.setVisibility(8);
//                    textView.setClickable(true);
//                    RequestSongDialog.this.dismiss();
//                }
//            });
//        }
//    }
}
