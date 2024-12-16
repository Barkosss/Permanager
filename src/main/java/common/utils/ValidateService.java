package common.utils;

import common.models.TimeZone;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.time.ZoneId;

/**
 * Валидация: Проверка строки на необходимое значение
 */
public class ValidateService {

    /**
     * Валидация числа и конвертация строки в число
     *
     * @param strInteger Строка с числом
     * @return Integer
     */
    public Optional<Integer> isValidInteger(String strInteger) {
        try {
            return Optional.of(Integer.parseInt(strInteger));
        } catch (Exception err) {
            return Optional.empty();
        }
    }


    /**
     * Валидация даты и конвертация строки в дату
     *
     * @param strLocalDate Строка с датой
     * @return LocalDate
     */
    public Optional<LocalDate> isValidDate(String strLocalDate) {

        String[] patterns = {
            "HH:mm dd.MM.yyyy",
            "HH:mm:ss dd.MM.yyyy",
            "HH:mm dd.MM.yy",
            "HH:mm:ss dd.MM.yy",
            "dd.MM.yyyy HH:mm",
            "dd.MM.yyyy HH:mm:ss",
            "dd.MM.yy HH:mm",
            "dd.MM.yy HH:mm:ss"
        };

        // Проходимся по каждому форматы дат
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return Optional.of(LocalDate.parse(strLocalDate, formatter));
            } catch (Exception err) {
                // ...
            }
        }
        return Optional.empty();
    }


    /**
     *
     */
    public Optional<LocalDate> isValidTime(String strLocalDate) {
        String[] patterns = {
            "HH:mm dd.MM.yyyy",
            "HH:mm:ss dd.MM.yyyy",
            "HH:mm:ss dd.MM.yyyy",
            "HH:mm dd.MM.yy",
            "HH:mm:ss dd.MM.yy",
            "dd.MM.yyyy HH:mm",
            "dd.MM.yyyy HH:mm:ss",
            "dd.MM.yy HH:mm",
            "dd.MM.yy HH:mm:ss"
        };

        // Проходимся по каждому форматы дат
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return Optional.of(LocalDate.parse(strLocalDate, formatter));
            } catch (Exception err) {
                break;
            }
        }
        return Optional.empty();
    }


    /**
     *
     */
    public Optional<TimeZone> isValidTimeZone(String strTimeZone) {
        try {
            return Optional.of(new TimeZone(ZoneId.of(formatterTimeZone(strTimeZone))));
        } catch (Exception err) {
            return Optional.empty();
        }
    }

    private String formatterTimeZone(String timeZone) {
        if (!timeZone.contains("/")) {
            return timeZone;
        }

        String[] parts = timeZone.split("/");
        return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase()
                + "/" + parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
    }
}