package com.example.animemusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.animemusic.R;

public class ShuffleButton extends AppCompatImageView {
    private ChangeStateListener listener;
    private int state = 0;

    public interface ChangeStateListener {
        void onChangeState(int i);
    }

    public ShuffleButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ShuffleButton.this.lambda$new$0$ShuffleButton(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ShuffleButton(View view) {
        if (this.state == 1) {
            this.state = 0;
        } else {
            this.state = 1;
        }
        ChangeStateListener changeStateListener = this.listener;
        if (changeStateListener != null) {
            changeStateListener.onChangeState(this.state);
        }
        setStateView();
    }

    private void setStateView() {
        if (this.state == 1) {
            setImageResource(R.drawable.ic_shuffle_on);
        } else {
            setImageResource(R.drawable.ic_shuffle);
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
        setStateView();
    }
}
