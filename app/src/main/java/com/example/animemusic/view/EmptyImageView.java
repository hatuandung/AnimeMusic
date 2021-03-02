package com.example.animemusic.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.animemusic.R;

public class EmptyImageView extends AppCompatImageView {
    private EmptyState state = EmptyState.GONE;

    public enum EmptyState {
        GONE,
        EMPTY,
        SEARCH
    }

    public EmptyImageView(Context context) {
        super(context);
    }

    public EmptyImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EmptyImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setState(EmptyState emptyState) {
        if (emptyState == EmptyState.EMPTY) {
            setVisibility(VISIBLE);
            setImageResource(R.drawable.ic_dissatisfied);
        } else if (emptyState == EmptyState.SEARCH) {
            setVisibility(VISIBLE);
            setImageResource(R.drawable.ic_search_24dp);
        } else {
            setVisibility(GONE);
        }
    }
}

