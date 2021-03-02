package com.example.animemusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.animemusic.models.AppStats;
import com.example.animemusic.models.Config;
import com.example.animemusic.models.Song;
import com.example.animemusic.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SharedPrefHelper {
    public static final String KEY_DEVICE_TOKEN = "key_device_token";
    public static final String KEY_FRAMES_REWARDED = "key_frame_rewarded";
    private static final String SETTINGS_NAME = "default_settings";
    private static SharedPrefHelper mSharedPrefs;
    private final String KEY_APP_CONFIGS = "key_app_configs";
    private final String KEY_APP_STATS = "key_app_stats";
    private final String KEY_QUEUE = "queue";
    private final String KEY_QUEUE_INDEX = "queue_index";
    private final String KEY_RECENT_SONGS = "recent_songs";
    private final String KEY_REPEAT_MODE = "repeat_mode";
    private final String KEY_SESSION_USER = "key_session_user";
    private final String KEY_SHUFFLE_MODE = "shuffle_mode";
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPref;

    private SharedPrefHelper() {
    }

    public SharedPrefHelper(Context context) {
        this.mPref = context.getSharedPreferences(SETTINGS_NAME, 0);
    }

    public static SharedPrefHelper getInstance(Context context) {
        if (mSharedPrefs == null) {
            mSharedPrefs = new SharedPrefHelper(context.getApplicationContext());
        }
        return mSharedPrefs;
    }

    public void put(String str, String str2) {
        doEdit();
        this.mEditor.putString(str, str2);
        doCommit();
    }

    public void put(String str, int i) {
        doEdit();
        this.mEditor.putInt(str, i);
        doCommit();
    }

    public void put(String str, long j) {
        doEdit();
        this.mEditor.putLong(str, j);
        doCommit();
    }

    public void put(String str, boolean z) {
        doEdit();
        this.mEditor.putBoolean(str, z);
        doCommit();
    }

    public String getString(String str) {
        return this.mPref.getString(str, (String) null);
    }

    public int getInt(String str) {
        return this.mPref.getInt(str, 0);
    }

    public int getInt(String str, int i) {
        return this.mPref.getInt(str, i);
    }

    public long getLong(String str, int i) {
        return this.mPref.getLong(str, (long) i);
    }

    public float getFloat(String str) {
        return this.mPref.getFloat(str, 0.0f);
    }

    public boolean getBool(String str, boolean z) {
        return this.mPref.getBoolean(str, z);
    }

    private void doEdit() {
        if (this.mEditor == null) {
            this.mEditor = this.mPref.edit();
        }
    }

    private void doCommit() {
        SharedPreferences.Editor editor = this.mEditor;
        if (editor != null) {
            editor.commit();
            this.mEditor = null;
        }
    }

    public void saveQueue(List<Song> list) {
        put("queue", new Gson().toJson((Object) list));
    }

    public List<Song> getQueue() {
        String string = getString("queue");
        ArrayList arrayList = new ArrayList();
        Gson gson = new Gson();
        try {
            JSONArray jSONArray = new JSONArray(string);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(gson.fromJson(jSONArray.get(i).toString(), Song.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void saveAppStats(AppStats appStats) {
        put("key_app_stats", new Gson().toJson((Object) appStats));
    }

    public AppStats getAppStats() {
        String string = getString("key_app_stats");
        AppStats appStats = new AppStats();
        if (string == null) {
            return appStats;
        }
        try {
            return (AppStats) new Gson().fromJson(string, AppStats.class);
        } catch (Exception e) {
            e.printStackTrace();
            return appStats;
        }
    }

    public void saveQueueIndex(int i) {
        put("queue_index", i);
    }

    public void nextQueueIndex() {
        int size = getQueue().size();
        int queueIndex = getQueueIndex(-1);
        if (size != 0 && queueIndex != -1) {
            saveQueueIndex((queueIndex + 1) % size);
        }
    }

    public void prevQueueIndex() {
        int size = getQueue().size();
        int queueIndex = getQueueIndex(-1);
        if (size != 0 && queueIndex != -1) {
            saveQueueIndex(queueIndex > 0 ? queueIndex - 1 : size - 1);
        }
    }

    public void setRepeatMode(int i) {
        put("repeat_mode", i);
    }

    public int getRepeatMode(int i) {
        return getInt("repeat_mode", i);
    }

    public int getShuffleMode(int i) {
        return getInt("shuffle_mode", i);
    }

    public void setShuffleMode(int i) {
        put("shuffle_mode", i);
    }

    public int getQueueIndex(int i) {
        return getInt("queue_index", i);
    }

    public void addRecentSong(Song song) {
        List<Song> recentSongs = getRecentSongs();
        recentSongs.remove(song);
        recentSongs.add(0, song);
        if (recentSongs.size() > 50) {
            recentSongs.remove(recentSongs.size() - 1);
        }
        put("recent_songs", new Gson().toJson((Object) recentSongs));
    }

    public void removeRecentSong(Song song) {
        List<Song> recentSongs = getRecentSongs();
        recentSongs.remove(song);
        if (recentSongs.size() > 50) {
            recentSongs.remove(recentSongs.size() - 1);
        }
        put("recent_songs", new Gson().toJson((Object) recentSongs));
    }

    public List<Song> getRecentSongs() {
        String string = getString("recent_songs");
        ArrayList arrayList = new ArrayList();
        Gson gson = new Gson();
        try {
            JSONArray jSONArray = new JSONArray(string);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(gson.fromJson(jSONArray.get(i).toString(), Song.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void saveConfig(Config config) {
        if (config != null) {
            put("key_app_configs", new Gson().toJson((Object) config));
        }
    }

    public Config getConfig() {
        return (Config) new Gson().fromJson(getString("key_app_configs"), Config.class);
    }

    public void saveSessionUser(User user) {
        put("key_session_user", new Gson().toJson((Object) user));
    }

    public User getSessionUser() {
        return (User) new Gson().fromJson(getString("key_session_user"), User.class);
    }

    public void saveFramesRewarded(Set<String> set) {
        put(KEY_FRAMES_REWARDED, new Gson().toJson((Object) set));
    }

    public Set<String> getRewardedFrames() {
        String string = getString(KEY_FRAMES_REWARDED);
        TreeSet treeSet = new TreeSet();
        try {
            JSONArray jSONArray = new JSONArray(string);
            for (int i = 0; i < jSONArray.length(); i++) {
                treeSet.add(jSONArray.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return treeSet;
    }
}