package com.example.animemusic.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.animemusic.utils.SongSuggestion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class LoadSuggestSearch extends AsyncTask<Void, Void, Void> {
    private final WeakReference<Context> weakActivity;

    public LoadSuggestSearch(Context context) {
        this.weakActivity = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = (Context) this.weakActivity.get();
        if (context != null) {
            try {
                SongSuggestion.getInstance().loadConfigs(new InputStreamReader(context.getAssets().open("suggest_search.csv")));
            } catch (IOException unused) {
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
