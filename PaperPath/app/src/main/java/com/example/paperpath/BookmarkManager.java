package com.example.paperpath;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BookmarkManager {

    private static final String PREF_NAME = "SmartCFP_Bookmarks";
    private static final String KEY_BOOKMARKED_IDS = "bookmarked_ids";
    private static final String KEY_INITIALIZED = "bookmarks_initialized";

    public static boolean isBookmarked(Context context, String confId) {
        if (context == null || confId == null) return false;
        ensureInitialData(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.contains("bm_" + confId)) {
            return prefs.getBoolean("bm_" + confId, false);
        }
        Set<String> set = prefs.getStringSet(KEY_BOOKMARKED_IDS, null);
        return set != null && set.contains(confId);
    }

    public static void toggleBookmark(Context context, String confId) {
        if (context == null || confId == null) return;
        boolean currentlyBookmarked = isBookmarked(context, confId);
        setBookmarked(context, confId, !currentlyBookmarked);
    }

    public static void setBookmarked(Context context, String confId, boolean bookmarked) {
        if (context == null || confId == null) return;
        ensureInitialData(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_BOOKMARKED_IDS, null);
        Set<String> newSet = (set != null) ? new HashSet<>(set) : new HashSet<>();
        if (bookmarked) {
            newSet.add(confId);
        } else {
            newSet.remove(confId);
        }
        prefs.edit()
                .putBoolean("bm_" + confId, bookmarked)
                .putStringSet(KEY_BOOKMARKED_IDS, newSet)
                .commit();
    }

    public static Set<String> getBookmarkedIds(Context context) {
        if (context == null) return new HashSet<>();
        ensureInitialData(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_BOOKMARKED_IDS, null);
        return (set != null) ? new HashSet<>(set) : new HashSet<>();
    }

    private static void ensureInitialData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            Set<String> defaults = new HashSet<>();
            // Pre-seed IEEE International Conference on AI as saved by default (as depicted in reference screenshot)
            defaults.add("ieee_ai_2026");
            prefs.edit()
                    .putBoolean("bm_ieee_ai_2026", true)
                    .putStringSet(KEY_BOOKMARKED_IDS, defaults)
                    .putBoolean(KEY_INITIALIZED, true)
                    .commit();
        }
    }
}
