package common.utils;

import common.enums.ErrorCategories;

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

    public void writeLog(String message, ErrorCategories category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFilePath, true))) {
            String log = String.format("[%s]\t[%s]\t%s", category.getValue(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(getTimeFormatter())), message);

            writer.write(log); // Форматируем и записываем сообщение в файл
            writer.newLine();  // Переход на новую строку
        } catch (IOException err) {
            System.out.printf("Log message isn't write in \"%s\" file\n", logFileName);
        }
    }

    public void writeLog(String message, ErrorCategories category, Boolean inConsole) {
        if (inConsole) {
            System.out.printf(category.getValue() + ": %s\n", message);
        }
    }

    public void info(String message) {
        writeLog(message, ErrorCategories.INFO);
    }

    public void debug(String message) {
        writeLog(message, ErrorCategories.DEBUG);
    }

    public void error(String message) {
        writeLog(message, ErrorCategories.ERROR);
    }

    public void warning(String message) {
        writeLog(message, ErrorCategories.WARNING);
    }

    public void trace(String message) {
        writeLog(message, ErrorCategories.TRACE);
    }

    public void fatal(String message) {
        writeLog(message, ErrorCategories.FATAL);
    }

    public void info(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.INFO, inConsole);
    }

    public void debug(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.DEBUG, inConsole);
    }

    public void error(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.ERROR, inConsole);
    }

    public void warning(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.WARNING, inConsole);
    }

    public void trace(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.TRACE, inConsole);
    }

    public void fatal(String message, Boolean inConsole) {
        writeLog(message, ErrorCategories.FATAL, inConsole);
    }
}