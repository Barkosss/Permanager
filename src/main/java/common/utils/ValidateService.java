package common.utils;

import common.models.TimeZone;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating and parsing string values such as numbers, dates,
 * durations, and time zones.
 */
public class ValidateService {

    private final LoggerHandler logger = new LoggerHandler();

    /**
     * Validates and parses a string as an Integer
     *
     * @param strInteger the string representing an integer value
     * @return an {@code Optional<Integer>} if parsing is successful, otherwise {@code Optional.empty()}
     */
    public Optional<Integer> isValidInteger(String strInteger) {
        try {
            return Optional.of(Integer.parseInt(strInteger));
        } catch (Exception err) {
            logger.debug(String.format("Failed to parse integer: '%s'", strInteger));
            return Optional.empty();
        }
    }

    /**
     * Validates and parses a string as a Long.
     *
     * @param strLong the string representing a long value
     * @return an {@code Optional<Long>} if parsing is successful, otherwise {@code Optional.empty()}
     */
    public Optional<Long> isValidLong(String strLong) {
        try {
            return Optional.of(Long.parseLong(strLong));
        } catch (Exception err) {
            logger.debug(String.format("Failed to parse long: '%s'", strLong));
            return Optional.empty();
        }
    }

    /**
     * Validates and parses a string into a {@code LocalDateTime} using multiple date patterns.
     *
     * @param strLocalDate the string representing a date and time
     * @return an {@code Optional<LocalDateTime>} if parsing is successful, otherwise {@code Optional.empty()}
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

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return Optional.of(LocalDateTime.parse(strLocalDate, formatter));
            } catch (Exception ignore) {
                // try next pattern
            }
        }

        logger.debug(String.format("Failed to recognize date format: '%s'", strLocalDate));
        return Optional.empty();
    }

    /**
     * Validates and parses a string into a future {@code LocalDateTime} based on a duration format.
     * Supported units: s (seconds), m (minutes), h (hours), d (days), w (weeks), mo (months), y (years)
     *
     * @param strDuration the string representing a time duration (e.g., "1h30m")
     * @return an {@code Optional<LocalDateTime>} representing the current time plus duration,
     * or {@code Optional.empty()} if parsing fails
     */
    public Optional<LocalDateTime> isValidDuration(String strDuration) {
        Pattern timePattern = Pattern.compile("(\\d+)(" + getMatchDesignations() + ")");

        if (strDuration.isEmpty()) {
            logger.debug("Duration string is empty.");
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
                default -> {
                    logger.debug(String.format("Unknown duration unit: '%s'", unit));
                    yield result;
                }
            };
        }

        if (!found) {
            logger.debug(String.format("Could not parse duration: '%s'", strDuration));
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

    /**
     * Builds the regex alternation pattern from supported time unit suffixes.
     *
     * @return a string like "s|m|h|d|w|mo|y"
     */
    private String getMatchDesignations() {
        return String.join("|", designations);
    }

    /**
     * Validates and parses a string into a {@code TimeZone} object.
     *
     * @param strTimeZone the string representing a time zone (e.g., "Europe/Moscow")
     * @return an {@code Optional<TimeZone>} if valid, otherwise {@code Optional.empty()}
     */
    public Optional<TimeZone> isValidTimeZone(String strTimeZone) {
        try {
            return Optional.of(new TimeZone(ZoneId.of(formatterTimeZone(strTimeZone))));
        } catch (Exception err) {
            logger.debug(String.format("Invalid time zone: '%s'", strTimeZone));
            return Optional.empty();
        }
    }

    /**
     * Normalizes a time zone string into proper case for parsing.
     * For example, "europe/moscow" becomes "Europe/Moscow".
     *
     * @param timeZone the time zone string to format
     * @return a formatted time zone string
     */
    private String formatterTimeZone(String timeZone) {
        if (!timeZone.contains("/")) {
            return timeZone;
        }

        String[] parts = timeZone.split("/");
        return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase()
                + "/" + parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
    }
}
