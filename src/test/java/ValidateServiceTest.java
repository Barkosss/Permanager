import common.models.TimeZone;
import common.utils.ValidateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ValidateServiceTest {
    ValidateService validate = new ValidateService();

    @Test
    public void testIsValidIntegerPositive() {
        Optional<Integer> parseInt = validate.isValidInteger("33");

        assertTrue(parseInt.isPresent());
        assertEquals(33, parseInt.get());
    }

    @Test
    @DisplayName("Negative, because there are other characters in the string than numbers")
    public void testIsValidIntegerNegative() {
        Optional<Integer> parseInt = validate.isValidInteger("14s");

        assertFalse(parseInt.isPresent());
    }

    @Test
    public void firstTestIsValidDatePositive() {
        String stringDate = "23:22 10.09.2022";

        LocalDateTime correctDate = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yyyy"));
        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    public void secondTestIsValidDatePositive() {
        String stringDate = "09:54:20 3.10.2024";

        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void thirdTestIsValidDatePositive() {
        String stringDate = "20:14 03.10.20";
        LocalDateTime correctDate = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yy"));
        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm:ss d.MM.yy")
    public void fourthTestIsValidDateNegative() {
        String stringDate = "14:10:21 1.12.21";

        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because the time in the clock exceeds 24 hours")
    public void fifthTestIsValidDateNegative() {
        String stringDate = "25:12 12.06.2023";

        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm dd/MM/yyyy")
    public void sixthTestIsValidDateNegative() {
        String stringDate = "21:12 12/06/2023";

        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm:ss dd/MM/yyyy")
    public void seventhTestIsValidDateNegative() {
        String stringDate = "21:12.15 12/06/2023";

        Optional<LocalDateTime> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Positively, because there is a time zone Europe/Moscow")
    public void firstTestIsValidTimeZonePositive() {
        String stringTimeZone = "Europe/Moscow";

        Optional<TimeZone> parseTimeZone = validate.isValidTimeZone(stringTimeZone);
        assertTrue(parseTimeZone.isPresent());
    }

    @Test
    @DisplayName("Positively, because the string will be formatted according to the desired format")
    public void secondTestIsValidTimeZonePositive() {
        String stringTimeZone = "europe/moscow";

        Optional<TimeZone> parseTimeZone = validate.isValidTimeZone(stringTimeZone);
        assertTrue(parseTimeZone.isPresent());
    }

    @Test
    @DisplayName("Positively, because the string will be formatted according to the desired format")
    public void thirdTestIsValidTimeZonePositive() {
        String stringTimeZone = "eUrope/moScow";

        Optional<TimeZone> parseTimeZone = validate.isValidTimeZone(stringTimeZone);
        assertTrue(parseTimeZone.isPresent());
    }

    @Test
    @DisplayName("Negatively, since there is no such time zone")
    public void fourthTestIsValidTimeZoneNegative() {
        String stringTimeZone = "Europe/Polish";

        Optional<TimeZone> parseTimeZone = validate.isValidTimeZone(stringTimeZone);
        assertFalse(parseTimeZone.isPresent());
    }
}