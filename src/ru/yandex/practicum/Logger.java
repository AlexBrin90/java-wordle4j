package ru.yandex.practicum;

import java.io.PrintWriter;

public class Logger {

    private final PrintWriter writer;

    public Logger(PrintWriter writer) {
        this.writer = writer;
    }

    public void info(String msg) {
        log("INFO", msg);
    }

    public void error(String msg) {
        log("ERROR", msg);
    }

    public void error(String msg, Throwable e) {
        log("ERROR", msg + ": " + e.getMessage());
        if (e != null) {
            e.printStackTrace(writer);
        }
    }

    public void debug(String msg) {
        log("DEBUG", msg);
    }

    private void log(String level, String msg) {
        writer.println("[" + level + "] " + msg);
        writer.flush();
    }
}