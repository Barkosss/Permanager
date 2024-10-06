package common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 *
 */
public class Validate {

    /**
     * Валидация числа и конвертация строки в число
     * @param strInteger Строка с числом
     * @return Integer
     */
    public Integer isValidInteger(String strInteger) {

        Optional<Integer> intValue = Optional.of(Integer.parseInt(strInteger));
        return intValue.orElse((null));
    }


    /**
     * Валидация даты и конвертация строки в дату
     * @param strLocalDate Строка с датой
     * @return LocalDate
     */
    public LocalDate isValidDate(String strLocalDate) {

        String[] patterns = {
                "H:mm dd.MM.yyyy",
                "H:mm:ss dd.MM.yyyy",
                "H:mm dd.MM.yy",
                "H:mm:ss dd.MM.yy"
        };

        for(String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate date = LocalDate.parse(strLocalDate, formatter);
                System.out.println("LocalDate: " + date);
                return date;
            } catch(Exception err) {
                continue;
            }
        }
        return null;
    }
}
