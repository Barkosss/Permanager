import common.utils.Validate;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class ValidateTest {
    public Validate validate = new Validate();

    @Test
    public void firstTestIsValidInteger() {
        assertEquals(33, validate.isValidInteger("33"));
    }

    @Test
    public void secondTestIsValidInteger() {
        assertNull(validate.isValidDate("12a"));
    }

    @Test
    public void firstTestIsValidDate() throws ParseException {
        String stringDate = "23:22 10.09.2022";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yyyy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNotNull(parseDate);
        assertEquals(correctDate, validate.isValidDate(stringDate));
    }

    @Test
    public void secondTestIsValidDate() throws ParseException {
        String stringDate = "09:54:20 3.10.2024";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm:ss d.MM.yyyy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNull(parseDate);
        assertNotEquals(correctDate, validate.isValidDate(stringDate));
    }

    @Test
    public void thirdTestIsValidDate() {
        String stringDate = "20:14 03.10.20";
        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd.MM.yy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNotNull(parseDate);
        assertEquals(correctDate, parseDate);
    }

    @Test
    public void fourthTestIsValidDate() throws ParseException {
        String stringDate = "14:10:21 1.12.21";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm:ss d.MM.yy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNull(parseDate);
        assertNotEquals(correctDate, validate.isValidDate(stringDate));
    }

    @Test
    public void fifthTestIsValidDate() throws ParseException {
        String stringDate = "25:12 12.06.2023";

        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNull(parseDate);
    }

    @Test
    public void sixthTestIsValidDate() throws ParseException {
        String stringDate = "21:12 12/06/2023";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm dd/MM/yyyy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNull(parseDate);
        assertNotEquals(correctDate, validate.isValidDate(stringDate));
    }

    @Test
    public void seventhTestIsValidDate() throws ParseException {
        String stringDate = "21:12.15 12/06/2023";

        LocalDate correctDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("H:mm.ss dd/MM/yyyy"));
        LocalDate parseDate = validate.isValidDate(stringDate);

        assertNull(parseDate);
        assertNotEquals(correctDate, validate.isValidDate(stringDate));
    }
}