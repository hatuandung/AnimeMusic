package com.example.animemusic.utils;

import androidx.exifinterface.media.ExifInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Filter {
    private static Filter instance;
    private int largestWordLength = 0;
    private Map<String, String[]> words = new HashMap();

    public static synchronized Filter getInstance() {
        Filter filter;
        synchronized (Filter.class) {
            if (instance == null) {
                instance = new Filter();
            }
            filter = instance;
        }
        return filter;
    }

    private Filter() {
    }

    public void loadConfigs(InputStreamReader inputStreamReader) {
        try {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int i = 0;
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    i++;
                    try {
                        String[] split = readLine.split(",");
                        if (split.length != 0) {
                            String str = split[0];
                            String[] strArr = new String[0];
                            if (split.length > 1) {
                                strArr = split[1].split("_");
                            }
                            if (str.length() > this.largestWordLength) {
                                this.largestWordLength = str.length();
                            }
                            this.words.put(str.replaceAll(" ", ""), strArr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PrintStream printStream = System.out;
                    printStream.println("Loaded " + i + " words to filter out");
                    return;
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private ArrayList<String> badWordsFound(String str) {
        boolean z;
        if (str == null) {
            return new ArrayList<>();
        }
        String replaceAll = str.replaceAll("1", "i").replaceAll("!", "i").replaceAll(ExifInterface.GPS_MEASUREMENT_3D, "e").replaceAll("4", "a").replaceAll("@", "a").replaceAll("5", "s").replaceAll("7", "t")/*.replaceAll(AppEventsConstants.EVENT_PARAM_VALUE_NO, "o")*/.replaceAll("9", "g");
        ArrayList<String> arrayList = new ArrayList<>();
        String lowerCase = replaceAll.toLowerCase();
        ArrayList arrayList2 = new ArrayList(Arrays.asList(lowerCase.split("[.\\s-_]+")));
        for (int i = 0; i < arrayList2.size(); i++) {
            String replaceAll2 = ((String) arrayList2.get(i)).replaceAll("[^a-zA-Z]", "");
            if (this.words.containsKey(replaceAll2)) {
                String[] strArr = this.words.get(replaceAll2);
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        z = false;
                        break;
                    } else if (lowerCase.contains(strArr[i2])) {
                        z = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z) {
                    arrayList.add(replaceAll2);
                }
            }
        }
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            PrintStream printStream = System.out;
            printStream.println(it.next() + " qualified as a bad word in a username");
        }
        return arrayList;
    }

    public String filterText(String str) {
        ArrayList<String> badWordsFound = badWordsFound(str);
        if (badWordsFound.size() <= 0) {
            return str;
        }
        for (int i = 0; i < badWordsFound.size(); i++) {
            char[] cArr = new char[badWordsFound.get(i).length()];
            Arrays.fill(cArr, '*');
            String valueOf = String.valueOf(cArr);
            str = str.replaceAll("(?i)" + badWordsFound.get(i), valueOf);
        }
        return str.replaceAll("\\w*\\*{4}", "****");
    }

    public boolean isBadWord(String str) {
        return badWordsFound(str).size() > 0;
    }
}