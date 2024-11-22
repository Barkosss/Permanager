package common.utils;

import java.io.FileWriter;
import java.io.IOException;

public class LoggerHandler {

    FileWriter logFile;

    String logFileName;

    public LoggerHandler() {
        this.logFileName = "";

        try {
            this.logFile = new FileWriter(logFileName);
        } catch (IOException e) {
            System.out.println("File with name \"" + logFileName + "\" isn't open");
        }
    }

    public void info(String message) {
        String log = "";
        try {
            this.logFile.write(log);
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }

    public void debug(String message) {
        String log = "";
        try {
            this.logFile.write(log);
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }
}