package com.example.animemusic.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.animemusic.R;
import com.example.animemusic.models.Migrate;
import com.example.animemusic.utils.SharedPrefHelper;

public class MigrateDialog extends Dialog {
    private final Context context;
    /* access modifiers changed from: private */
    public DialogListener listener;

    public interface DialogListener {
        void onClose();

        void onMore();

        void onOk();
    }

    public MigrateDialog(Context context2) {
        super(context2);
        this.context = context2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_migrate);
        final Migrate migrate = SharedPrefHelper.getInstance(getContext()).getConfig().getMigrate();
        TextView textView = (TextView) findViewById(R.id.app_title);
        ImageView imageView = (ImageView) findViewById(R.id.icon);
        Button button = (Button) findViewById(R.id.btn_info);
        Button button2 = (Button) findViewById(R.id.btn_ok);
        ((TextView) findViewById(R.id.title)).setText(migrate.getTitle());
        ((TextView) findViewById(R.id.desc)).setText(migrate.getDescription());
        textView.setText(migrate.getAppTitle());
        ((TextView) findViewById(R.id.app_desc)).setText(migrate.getAppDesc());
        button.setText(migrate.getBtn1Label());
        button2.setText(migrate.getBtn2Label());
        ((RequestBuilder) Glide.with(this.context).load(migrate.getAppIconUrl()).centerCrop()).into(imageView);
        setCanceledOnTouchOutside(false);
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MigrateDialog.this.goToApp(migrate.getUrl2());
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MigrateDialog.this.goToApp(migrate.getUrl2());
            }
        });
        ((ImageView) findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MigrateDialog.this.listener != null) {
                    MigrateDialog.this.listener.onClose();
                }
                MigrateDialog.this.cancel();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MigrateDialog.this.listener != null) {
                    MigrateDialog.this.listener.onOk();
                }
                MigrateDialog.this.goToApp(migrate.getUrl2());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MigrateDialog.this.listener != null) {
                    MigrateDialog.this.listener.onMore();
                }
                MigrateDialog.this.goToApp(migrate.getUrl1());
            }
        });
    }

    public void setListener(DialogListener dialogListener) {
        this.listener = dialogListener;
    }

    /* access modifiers changed from: private */
    public void goToApp(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        intent.setPackage("com.android.vending");
        this.context.startActivity(intent);
    }
}
