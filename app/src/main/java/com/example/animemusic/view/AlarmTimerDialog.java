package com.example.animemusic.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.animemusic.R;
import com.example.animemusic.utils.Helper;


public class AlarmTimerDialog extends Dialog {
    /* access modifiers changed from: private */
    public int alarmMinute = 0;
    private AlarmTimerListener listener;
    /* access modifiers changed from: private */
    public ToggleButton toggleTimer;
    /* access modifiers changed from: private */
    public TextView tvTime;

    public interface AlarmTimerListener {
        void onCancel();

        void onOk(int i);
    }

    public AlarmTimerDialog(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.dialog_select_alarm);
        AppCompatSeekBar appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar_timer);
        this.toggleTimer = (ToggleButton) findViewById(R.id.toggle_timer);
        this.tvTime = (TextView) findViewById(R.id.tv_timer);
        ((TextView) findViewById(R.id.tv_min_time)).setText("0'");
        ((TextView) findViewById(R.id.tv_max_time)).setText("120'");
        appCompatSeekBar.setMax(120);
        appCompatSeekBar.setProgress(this.alarmMinute);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Log.d("mylog", "minute " + i);
                int unused = AlarmTimerDialog.this.alarmMinute = i;
                AlarmTimerDialog.this.tvTime.setText(Helper.minuteToString(AlarmTimerDialog.this.alarmMinute));
                if (AlarmTimerDialog.this.alarmMinute == 0) {
                    AlarmTimerDialog.this.toggleTimer.setChecked(false);
                } else {
                    AlarmTimerDialog.this.toggleTimer.setChecked(true);
                }
            }
        });
        ((Button) findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AlarmTimerDialog.this.lambda$onCreate$0$AlarmTimerDialog(view);
            }
        });
        int i = this.alarmMinute;
        if (i == 0) {
            this.toggleTimer.setChecked(false);
            appCompatSeekBar.setProgress(this.alarmMinute);
            this.tvTime.setText(Helper.minuteToString(this.alarmMinute));
            return;
        }
        appCompatSeekBar.setProgress(i);
        this.toggleTimer.setChecked(true);
        this.tvTime.setText(Helper.minuteToString(this.alarmMinute));
    }

    public /* synthetic */ void lambda$onCreate$0$AlarmTimerDialog(View view) {
        if (this.listener != null) {
            if (this.toggleTimer.isChecked()) {
                this.listener.onOk(this.alarmMinute);
            } else {
                this.listener.onCancel();
            }
        }
        cancel();
    }

    public void setListener(AlarmTimerListener alarmTimerListener) {
        this.listener = alarmTimerListener;
    }

    public void setAlarmMinute(int i) {
        if (i < 0 || i > 120) {
            this.alarmMinute = 0;
        } else {
            this.alarmMinute = i;
        }
    }
}