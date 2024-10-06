package common.iostream;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для чтения
 */
public interface Input {

    /**
     * Считать число и её вернуть
     * @return Integer
     */
    int getInt();

    /**
     * Считать строку и её вернуть
     * @return String
     */
    String getString();

    /**
     * Считать строку и вернуть список аргументов
     * @param separator Разделитель аргументовы
     * @return List
     */
    List<String> getStrings(String separator);

    /**
     * Считать строку, парснуть в дату LocalDate и её вернуть
     * @return LocalDate
     */
    LocalDate getDate();
}