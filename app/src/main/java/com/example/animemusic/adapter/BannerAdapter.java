package com.example.animemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animemusic.R;
import com.example.animemusic.models.Playlist;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;

import java.util.List;

public class BannerAdapter extends EnchantedViewPagerAdapter {
    private List bannerPlaylist;
    private LayoutInflater inflater;
    private BannerAdapterListener mListener;

    public interface BannerAdapterListener {
        void onClick(int i);
    }

    public int getItemPosition(Object obj) {
        return -2;
    }

    public float getPageWidth(int i) {
        return 0.87f;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public BannerAdapter(Context context, List list) {
        super(list);
        this.inflater = LayoutInflater.from(context);
        this.bannerPlaylist = list;
    }

    public void setOnClickListener(BannerAdapterListener bannerAdapterListener) {
        this.mListener = bannerAdapterListener;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {


        View inflate = this.inflater.inflate(R.layout.row_banner, viewGroup, false);
        RoundedImageView roundedImageView = (RoundedImageView) inflate.findViewById(R.id.banner_background);
        TextView textView = (TextView) inflate.findViewById(R.id.title);
        Playlist playlist = (Playlist) this.bannerPlaylist.get(i);
        if (textView != null) {
            textView.setText(playlist.getName());
        }
        Picasso.get().load(playlist.getThumbnailUrl()).into((ImageView) roundedImageView);
        inflate.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + i);
        viewGroup.addView(inflate);

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(i);
            }
        });

        return inflate;

    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

    public int getCount() {
        return this.bannerPlaylist.size();
    }
}
