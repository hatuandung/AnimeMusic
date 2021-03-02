package com.example.animemusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.animemusic.R;

public class RepeatButton extends AppCompatImageView {
    private ChangeStateListener listener;
    private int state = 2;

    public interface ChangeStateListener {
        void onChangeState(int i);
    }

    public RepeatButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RepeatButton.this.lambda$new$0$RepeatButton(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$RepeatButton(View view) {
        int i = this.state;
        if (i == 0) {
            this.state = 2;
        } else if (i == 2) {
            this.state = 1;
        } else {
            this.state = 0;
        }
        ChangeStateListener changeStateListener = this.listener;
        if (changeStateListener != null) {
            changeStateListener.onChangeState(this.state);
        }
        setState();
    }

    private void setState() {
        int i = this.state;
        if (i == 0) {
            setImageResource(R.drawable.ic_repeat_off);
        } else if (i == 1) {
            setImageResource(R.drawable.ic_repeat_one);
        } else {
            setImageResource(R.drawable.ic_repeat_on);
        }
    }

    public void setListener(ChangeStateListener changeStateListener) {
        this.listener = changeStateListener;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int i) {
        this.state = i;
        setState();
    }
}
