package common.iostream;

import common.models.Interaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для чтения
 */
public interface Input {

    /**
     * Считать строку и её вернуть
     * @return String
     */
    String getString(Interaction interaction);

    /**
     * Считать строку и вернуть список аргументов
     * @param separator Разделитель аргументов
     * @return List
     */
    List<String> getString(Interaction interaction, String separator);

    /**
     * Считать число и её вернуть
     * @return Integer
     */
    int getInt(Interaction interaction);

    /**
     * Считать строку, парснуть в дату LocalDate и её вернуть
     * @return LocalDate
     */
    LocalDate getDate(Interaction interaction);
}