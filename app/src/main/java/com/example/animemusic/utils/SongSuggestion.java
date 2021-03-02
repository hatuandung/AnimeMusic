package com.example.animemusic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SongSuggestion {
    private static SongSuggestion instance;
    private final List<String> suggests = new ArrayList();

    public static synchronized SongSuggestion getInstance() {
        SongSuggestion songSuggestion;
        synchronized (SongSuggestion.class) {
            if (instance == null) {
                instance = new SongSuggestion();
            }
            songSuggestion = instance;
        }
        return songSuggestion;
    }

    private SongSuggestion() {
    }

    public void loadConfigs(InputStreamReader inputStreamReader) {
        try {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    suggests.add(readLine);
                } else {
                    return;
                }
            }
        } catch (IOException unused) {
        }
    }

    public List<String> getSuggests() {
        return suggests;
    }
}
