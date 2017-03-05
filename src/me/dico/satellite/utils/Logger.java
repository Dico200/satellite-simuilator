package me.dico.satellite.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Logger {

    private HashMap<Long, String> logs;
    private String logName;

    private boolean debugging = false;

    public Logger(String name) {
        logs = new HashMap<Long, String>();
        logName = name;
    }

    public void setDebugging(boolean value) {
        debugging = value;
    }

    public void debug(String string) {
        logs.put(System.currentTimeMillis(), "[DEBUG]" + string);
        if (!debugging) return;
        String time = parseTime(System.currentTimeMillis());
        System.out.println(String.format("[DEBUG][%s][%s]%s", time, logName, string));
    }

    public void log(String string) {
        logs.put(System.currentTimeMillis(), string);
        String time = parseTime(System.currentTimeMillis());
        System.out.println(String.format("[%s][%s] %s", time, logName, string));
    }

    public void rawln(String string) {
        logs.put(System.currentTimeMillis(), string);
        System.out.println(string);
    }

    public void raw(String string) {
        logs.put(System.currentTimeMillis(), string);
        System.out.print(string);
    }

    public String parseTime(Long time) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
    }
}
