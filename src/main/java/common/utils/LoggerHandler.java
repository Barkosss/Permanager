package common.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerHandler {

    FileWriter logFile;

    String logFileName;

    String logFilePath;
    
    private String getTimeFormatter() {
        return "dd-MM-yyyy HH:mm:ss";
    }

    public LoggerHandler() {
        this.logFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".log";
        this.logFilePath = "./src/main/resources/logs/" + logFileName;

        try {
            this.logFile = new FileWriter(this.logFilePath, true);
            info("Logger is start");
        } catch (IOException e) {
            System.out.println("File with name \"" + logFileName + "\" isn't open");
        }
    }

    public void info(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = "[INFO]\t" +
                    "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())) + "]\t" +
                    message;

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }

    public void debug(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = "[DEBUG]\t" +
                    "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())) + "]\t" +
                    message;

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }

    public void error(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = "[ERROR]\t" +
                    "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())) + "]\t" +
                    message;

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.println("Log message isn't write in \"" + logFileName + "\" file");
        }
    }
}