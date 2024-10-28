package common.iostream;

import common.models.AbstractInteraction;
import common.models.InteractionTelegram;

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
    String getString();

    /**
     * Считать строку и вернуть список аргументов
     * @param separator Разделитель аргументовы
     * @return List
     */
    List<String> getString(InteractionTelegram interaction, String separator);

    /**
     * Считать число и её вернуть
     * @return Integer
     */
    int getInt(InteractionTelegram interaction);

    /**
     * Считать строку, парснуть в дату LocalDate и её вернуть
     * @return LocalDate
     */
    LocalDate getDate(InteractionTelegram interaction);
}