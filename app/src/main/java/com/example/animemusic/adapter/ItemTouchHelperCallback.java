package com.example.animemusic.adapter;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchListener itemTouchListener;

    public interface ItemTouchListener {
        void onMove(int i, int i2);
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
    }

    public ItemTouchHelperCallback(ItemTouchListener itemTouchListener2) {
        this.itemTouchListener = itemTouchListener2;
    }

    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(3, 12);
    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        this.itemTouchListener.onMove(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
        return false;
    }
}
