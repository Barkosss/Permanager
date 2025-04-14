package common.utils;

import common.enums.LoggerStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Как выбирать уровень?

 * Обычная успешная работа? — INFO
 * Неправильный вход пользователя? — WARN
 * Сломалась важная операция (но не всё приложение)? — ERROR
 * Приложение не может работать дальше? — FATAL
 * Тебе нужно отследить, что пошло не так? — DEBUG
 * Хочешь следить за каждым шагом метода? — TRACE
 */

public class LoggerHandler {

    private static boolean debugMode;

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
        createLogger();
    }

    public LoggerHandler(String[] args) {
        if (args.length >= 2) {
            debug("Checking args for debug mode: " + args[1]);
            setDebugMode(args[1].toLowerCase().contains("debug"));
        }

        createLogger();
    }

    public void setDebugMode(boolean debugMode) {
        writeLog("Debug mode is enable", LoggerStatus.DEBUG);
        LoggerHandler.debugMode = debugMode;
    }

    private void createLogger() {
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

    public void writeLog(String message, LoggerStatus status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[%s]\t[%s]\t%s", status.getName(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void writeLog(String message, LoggerStatus status, Boolean inConsole) {
        if (inConsole) {
            System.out.printf(status.getName() + ": %s\n", message);
        }
        writeLog(message, status);
    }

    public void newLine() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("New line in log isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void info(String message) {
        writeLog(message, LoggerStatus.INFO);
    }

    public void info(String message, Boolean inConsole) {
        writeLog(message, LoggerStatus.INFO, inConsole);
    }

    public void debug(String message) {
        if (!debugMode) return;
        writeLog(message, LoggerStatus.DEBUG);
    }

    public void debug(String message, Boolean inConsole) {
        if (!debugMode) return;
        writeLog(message, LoggerStatus.DEBUG, inConsole);
    }

    public void error(String message) {
        writeLog(message, LoggerStatus.ERROR);
    }

    public void error(String message, Boolean inConsole) {
        writeLog(message, LoggerStatus.ERROR, inConsole);
    }

    public void warning(String message) {
        writeLog(message, LoggerStatus.WARNING);
    }

    public void warning(String message, Boolean inConsole) {
        writeLog(message, LoggerStatus.WARNING, inConsole);
    }

    public void trace(String message) {
        writeLog(message, LoggerStatus.TRACE);
    }

    public void fatal(String message) {
        writeLog(message, LoggerStatus.FATAL);
    }

    public void fatal(String message, Boolean inConsole) {
        writeLog(message, LoggerStatus.FATAL, inConsole);
    }
}
