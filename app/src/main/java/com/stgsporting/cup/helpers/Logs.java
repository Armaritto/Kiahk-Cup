package com.stgsporting.cup.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class Logs {

    public static void log(Context context, String msg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Logs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(System.currentTimeMillis()), msg);
        editor.apply();
    }

    public static String[] getLogs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Logs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        String[] logs = new String[allEntries.size()];
        int i = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            logs[i] = entry.getKey() + " : " + entry.getValue().toString();
            i++;
        }
        return logs;
    }

}
