import common.utils.Validate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ValidateTest {
    public Validate validate = new Validate();

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

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yyyy"));
        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    public void secondTestIsValidDatePositive() {
        String stringDate = "09:54:20 3.10.2024";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void thirdTestIsValidDatePositive() {
        String stringDate = "20:14 03.10.20";
        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yy"));
        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm:ss d.MM.yy")
    public void fourthTestIsValidDateNegative() {
        String stringDate = "14:10:21 1.12.21";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because the time in the clock exceeds 24 hours")
    public void fifthTestIsValidDateNegative() {
        String stringDate = "25:12 12.06.2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm dd/MM/yyyy")
    public void sixthTestIsValidDateNegative() {
        String stringDate = "21:12 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    @DisplayName("Negative because there is no format: H:mm:ss dd/MM/yyyy")
    public void seventhTestIsValidDateNegative() {
        String stringDate = "21:12.15 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }
}