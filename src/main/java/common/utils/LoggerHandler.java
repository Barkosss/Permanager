package common.utils;

import common.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerHandler {

    // Объект файла
    FileWriter logFile;

    // Название лог файла
    String logFileName;

    // Путь к лог файлу
    String logFilePath;

    private String getTimeFormatter() {
        return "dd-MM-yyyy HH:mm:ss";
    }

    public LoggerHandler() {
        LocalDateTime dateNow = LocalDateTime.now();
        this.logFileName = String.format("%s.log", dateNow.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        this.logFilePath = String.format("./src/main/resources/logs/%s-%s/%s", dateNow.getMonthValue(),
                dateNow.getYear(), logFileName);

        try {
            Path path = Path.of(String.format("./src/main/resources/logs/%s-%s", dateNow.getMonthValue(),
                    dateNow.getYear()));

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

    public void debug(String message, boolean inConsole) {
        if (inConsole && Main.arguments.contains("debug")) {
            System.out.printf("DEBUG: %s\n", message);
        }
        debug(message);
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