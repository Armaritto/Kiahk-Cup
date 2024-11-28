package com.stgsporting.cup.data;

public class Log {

    private long time;
    private String log;

    public Log(String time, String log) {
        this.time = Long.parseLong(time);
        this.log = log;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getCompLog() {
        return time+" : "+log;
    }

}
