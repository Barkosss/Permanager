import common.utils.Validate;
import org.junit.jupiter.api.Test;

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
    public void fourthыTestIsValidDateNegative() {
        String stringDate = "14:10:21 1.12.21";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void fifthTestIsValidDateNegative() {
        String stringDate = "25:12 12.06.2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void sixthTestIsValidDateNegative() {
        String stringDate = "21:12 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void seventhTestIsValidDateNegative() {
        String stringDate = "21:12.15 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }
}