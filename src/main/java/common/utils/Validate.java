package common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * Валидация: Проверка строки на необходимое значение
 */
public class Validate {

    /**
     * Валидация числа и конвертация строки в число
     * @param strInteger Строка с числом
     * @return Integer
     */
    public Optional<Integer> isValidInteger(String strInteger) {
        return Optional.of(Integer.parseInt(strInteger));
    }


    /**
     * Валидация даты и конвертация строки в дату
     * @param strLocalDate Строка с датой
     * @return LocalDate
     */
    public Optional<LocalDate> isValidDate(String strLocalDate) {

        String[] patterns = {
                "H:mm dd.MM.yyyy",
                "H:mm:ss dd.MM.yyyy",
                "H:mm dd.MM.yy",
                "H:mm:ss dd.MM.yy"
        };

        // Проходимся по каждому форматы дат
        for(String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return Optional.of(LocalDate.parse(strLocalDate, formatter));
            } catch(Exception err) {
                continue;
            }
        }
        return null;
    }
}
