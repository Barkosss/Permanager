package common.utils;

import common.models.TimeZone;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Валидация: Проверка строки на необходимое значение
 */
public class ValidateService {

    /**
     * Валидация числа и конвертация строки в число (Integer)
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
     * Валидация числа и конвертация строки в число (Long)
     *
     * @param strLong Строка с числом (Long)
     * @return Long
     */
    public Optional<Long> isValidLong(String strLong) {
        try {
            return Optional.of(Long.parseLong(strLong));
        } catch (Exception err) {
            return Optional.empty();
        }
    }


    /**
     * Валидация даты и конвертация строки в дату
     *
     * @param strLocalDate Строка с датой
     * @return LocalDateTime
     */
    public Optional<LocalDateTime> isValidDate(String strLocalDate) {

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
                return Optional.of(LocalDateTime.parse(strLocalDate, formatter));
            } catch (Exception err) {
                // The exception is ignored for a specific reason
            }
        }
        return Optional.empty();
    }


    /**
     * Валидация времени и конвертация строки на время
     *
     * @param strLocalTime Строка со временем
     * @return LocalDateTime
     */
    public Optional<LocalDateTime> isValidTime(String strLocalTime) {
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
                return Optional.of(LocalDateTime.parse(strLocalTime, formatter));
            } catch (Exception err) {
                break;
            }
        }
        return Optional.empty();
    }


    /**
     * @param strDuration Строка длительности
     * @return Возвращает Optional<LocalDateTime>
     */
    public Optional<LocalDateTime> isValidDuration(String strDuration) {
        Pattern timePattern = Pattern.compile("(\\d+)(" + getMatchDesignations() + ")");

        if (strDuration.isEmpty()) {
            return Optional.empty();
        }

        Matcher matcher = timePattern.matcher(strDuration);
        LocalDateTime result = LocalDateTime.now();
        boolean found = false;

        while (matcher.find()) {
            found = true;

            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            result = switch (unit) {
                case "s" -> result.plusSeconds(value);
                case "m" -> result.plusMinutes(value);
                case "h" -> result.plusHours(value);
                case "d" -> result.plusDays(value);
                case "w" -> result.plusWeeks(value);
                case "mo" -> result.plusMonths(value);
                case "y" -> result.plusYears(value);
                default -> result;
            };
        }

        return found ? Optional.of(result) : Optional.empty();
    }

    private final String[] designations = new String[]{
            "s",
            "m",
            "h",
            "d",
            "w",
            "mo",
            "y"
    };

    private String getMatchDesignations() {
        return String.join("|", designations);
    }


    /**
     * Валидация часового пояса и конвертация строки в объект
     *
     * @param strTimeZone Строка с часовым поясом
     * @return TimeZone
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
