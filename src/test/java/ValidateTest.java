import common.utils.Validate;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ValidateTest {
    public Validate validate = new Validate();

    @Test
    public void firstTestIsValidInteger() {
        Optional<Integer> parseInt = validate.isValidInteger("33");

        assertTrue(parseInt.isPresent());
        assertEquals(33, parseInt.get());
    }

    @Test
    public void secondTestIsValidInteger() {
        Optional<Integer> parseInt = validate.isValidInteger("14s");

        assertTrue(parseInt.isEmpty());
    }

    @Test
    public void firstTestIsValidDate() throws ParseException {
        String stringDate = "23:22 10.09.2022";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yyyy"));
        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    public void secondTestIsValidDate() throws ParseException {
        String stringDate = "09:54:20 3.10.2024";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void thirdTestIsValidDate() {
        String stringDate = "20:14 03.10.20";
        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yy"));
        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isPresent());
        assertEquals(correctDate, parseDate.get());
    }

    @Test
    public void fourthTestIsValidDate() throws ParseException {
        String stringDate = "14:10:21 1.12.21";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void fifthTestIsValidDate() throws ParseException {
        String stringDate = "25:12 12.06.2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void sixthTestIsValidDate() throws ParseException {
        String stringDate = "21:12 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }

    @Test
    public void seventhTestIsValidDate() throws ParseException {
        String stringDate = "21:12.15 12/06/2023";

        Optional<LocalDate> parseDate = validate.isValidDate(stringDate);

        assertTrue(parseDate.isEmpty());
    }
}