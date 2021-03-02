package com.example.animemusic.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.palette.graphics.Palette;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Color {
    public static void loadBgBannerAverage(int i, View view, String str) {
        final WeakReference weakReference = new WeakReference(view);
        Picasso.get().load(str).resize(i, i).centerCrop().into((Target) new Target() {
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                int access$000 = Color.getDominantColor(bitmap);
                final View view = (View) weakReference.get();
                try {
                    Drawable background = view.getBackground();
                    int color = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : 0;
                    ValueAnimator valueAnimator = new ValueAnimator();
                    valueAnimator.setIntValues(new int[]{color, access$000});
                    valueAnimator.setEvaluator(new ArgbEvaluator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                        }
                    });
                    valueAnimator.setDuration(300);
                    valueAnimator.start();
                } catch (Exception unused) {
                    view.setBackgroundColor(access$000);
                }
            }

            public void onBitmapFailed(Exception exc, Drawable drawable) {
                Log.d("swipe", "failed" + drawable);
            }

            public void onPrepareLoad(Drawable drawable) {
                Log.d("swipe", "prepared");
            }
        });
    }

    public static int getDominantColor(Bitmap bitmap) {
        ArrayList arrayList = new ArrayList(Palette.from(bitmap).generate().getSwatches());
        Collections.sort(arrayList, new Comparator<Palette.Swatch>() {
            public int compare(Palette.Swatch swatch, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch.getPopulation();
            }
        });
        if (arrayList.size() > 0) {
            return ((Palette.Swatch) arrayList.get(0)).getRgb();
        }
        return 0;
    }

}
