package com.example.animemusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.animemusic.R;

public class FavoriteButton extends AppCompatImageView {
    private boolean isFavorite = false;
    private ChangeStateListener listener;

    public interface ChangeStateListener {
        void onChangeState(boolean z);
    }

    public FavoriteButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FavoriteButton.this.lambda$new$0$FavoriteButton(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$FavoriteButton(View view) {
        boolean z = !this.isFavorite;
        this.isFavorite = z;
        ChangeStateListener changeStateListener = this.listener;
        if (changeStateListener != null) {
            changeStateListener.onChangeState(z);
        }
        setState(this.isFavorite);
    }

    private void setStateView() {
        if (this.isFavorite) {
            setImageResource(R.drawable.ic_favorite_active);
        } else {
            setImageResource(R.drawable.ic_favorite);
        }
    }

    public void setListener(ChangeStateListener changeStateListener) {
        this.listener = changeStateListener;
    }

    public boolean getState() {
        return this.isFavorite;
    }

    public void setState(boolean z) {
        this.isFavorite = z;
        setStateView();
    }
}
