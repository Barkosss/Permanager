package common.iostream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ЗАГЛУШКИ

public class InputDiscord implements Input {

    /**
     * Считать число и её вернуть
     * @return Integer
     */
    public int getInt() {
        return 0;
    }

    /**
     * Считать строку и её вернуть
     * @return String
     */
    public String getString() {
        return "test";
    }

    /**
     * Считать строку и вернуть список аргументов
     * @param separator Разделитель аргументов
     * @return List
     */
    public List<String> getString(String separator) {
        return List.of("1", "2");
    }

    /**
     * Считать строку, парснуть в дату LocalDate и её вернуть
     * @return LocalDate
     */
    public LocalDate getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm dd.MM.yyyy");
        return LocalDate.parse("00:53 7.10.2024", formatter);
    }
}
