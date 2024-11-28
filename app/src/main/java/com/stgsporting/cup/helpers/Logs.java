package com.stgsporting.cup.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.data.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public class Logs {

    public static void log(Context context, String msg, int num) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Logs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(System.currentTimeMillis())+num, msg);
        editor.apply();
    }

    public static String[] getLogs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Logs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        String[] logs = new String[allEntries.size()];
        ArrayList<Log> logArrayList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            logArrayList.add(new Log(entry.getKey(), entry.getValue().toString()));
        }
        logArrayList.sort((log, t1) -> (int) (log.getTime()-t1.getTime()));
        for (int i=0;i<logArrayList.size();i++) {
            logs[i] = logArrayList.get(i).getCompLog();
        }
        return logs;
    }

}
