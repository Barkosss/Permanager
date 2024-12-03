package common.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        this.logFileName = String.format("%s.log", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        this.logFilePath = String.format("./src/main/resources/logs/%s-%s/%s", LocalDateTime.now().getMonthValue(),
                LocalDateTime.now().getYear(), logFileName);

        try {
            Path path = Path.of(String.format("./src/main/resources/logs/%s-%s", LocalDateTime.now().getMonthValue(),
                    LocalDateTime.now().getYear()));

            // Если не существует директории, то создать
            if (!Files.exists(path)) {
                Files.createDirectories(path);

            }
            this.logFile = new FileWriter(this.logFilePath, true);
        } catch (IOException e) {
            System.out.printf("File with name \"%s\" isn't open\n", logFileName);
        }
    }

    public void info(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[INFO]\t[%s]\t%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void info(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("INFO: %s\n", message);
        }
        info(message);
    }

    public void debug(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[DEBUG]\t[%s]\t%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void error(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[ERROR]\t[%s]\t%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void error(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("ERROR: %s\n", message);
        }
        error(message);
    }
}