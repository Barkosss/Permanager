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

    // Статусы логгера
    enum LoggerStatus {
        INFO("INFO"),
        DEBUG("DEBUG"),
        ERROR("ERROR"),
        WARNING("WARNING"),
        CRITICAL("CRITICAL");

        private final String value;

        LoggerStatus(String critical) {
            this.value = critical;
        }

        public String getName() {
            return value;
        }
    }

    // Объект файла
    FileWriter logFile;

    // Название лог файла
    String logFileName;

    // Путь к лог файлу
    String logFilePath;

    // Формат времени
    private String getTimeFormatter() {
        return "dd-MM-yyyy HH:mm:ss";
    }

    // Конструктор логгера
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

    // Запись лога в файл
    private void createLog(String message, LoggerStatus status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[%s]\t[%s]\t%s",
                    status.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void info(String message) {
        createLog(message, LoggerStatus.INFO);
    }

    public void info(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("INFO: %s\n", message);
        }
        createLog(message, LoggerStatus.INFO);
    }

    public void debug(String message) {
        createLog(message, LoggerStatus.DEBUG);
    }

    public void debug(String message, boolean inConsole) {
        if (inConsole && Main.arguments.contains("debug")) {
            System.out.printf("DEBUG: %s\n", message);
        }
        createLog(message, LoggerStatus.DEBUG);
    }

    public void error(String message) {
        createLog(message, LoggerStatus.ERROR);
    }

    public void error(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("ERROR: %s\n", message);
        }
        createLog(message, LoggerStatus.ERROR);
    }

    public void warning(String message) {
        createLog(message, LoggerStatus.WARNING);
    }

    public void warning(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("WARNING: %s\n", message);
        }
        createLog(message, LoggerStatus.WARNING);
    }

    public void critical(String message) {
        createLog(message, LoggerStatus.CRITICAL);
    }

    public void critical(String message, boolean inConsole) {
        if (inConsole) {
            System.out.printf("CRITICAL: %s\n", message);
        }
        createLog(message, LoggerStatus.CRITICAL);
    }
}