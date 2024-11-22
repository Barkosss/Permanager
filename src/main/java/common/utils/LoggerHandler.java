package common.utils;

import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class LoggerHandler {

    FileWriter logFile;

    String logFileName;

    public LoggerHandler() {
        this.logFileName = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".log";

        try {
            this.logFile = new FileWriter("./src/main/resources/logs/" + logFileName, true);
        } catch (IOException e) {
            System.out.println("File with name \"" + logFileName + "\" isn't open");
        }
    }

    public void info(String message) {
        StringBuilder log = new StringBuilder();
        log.append("[INFO]\t");
        log.append("[").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("]\t");
        log.append(message);

        System.out.println(log);
        try {
            logFile.write(String.valueOf(log));
            System.out.println("write");
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }

    public void debug(String message) {
        StringBuilder log = new StringBuilder();
        log.append("[DEBUG]\t");
        log.append("[").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("]\t");
        log.append(message);

        try {
            this.logFile.write(String.valueOf(log));
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }

    public void error(String message) {
        StringBuilder log = new StringBuilder();
        log.append("[ERROR]\t");
        log.append("[").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("]\t");
        log.append(message);

        try {
            this.logFile.write(String.valueOf(log));
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }
}