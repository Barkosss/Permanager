import common.utils.Validate;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String date = "23:22 10.09.2022";
        Date correctDate = new SimpleDateFormat("H:mm dd.MM.yyyy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void secondTestIsValidDate() throws ParseException {
        String date = "09:54:20 3.10.2024";
        Date correctDate = new SimpleDateFormat("H:mm:ss dd.MM.yyyy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void thirdTestIsValidDate() {
        String date = "20:14 3.10.20";
        Date correctDate = new SimpleDateFormat("H:mm dd.MM.yy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void fourthTestIsValidDate() throws ParseException {
        String date = "14:10:21 1.12.21";
        Date correctDate = new SimpleDateFormat("H:mm:ss dd.MM.yy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void fifthTestIsValidDate() throws ParseException {
        String date = "25:12 12.06.2023";
        Date correctDate = new SimpleDateFormat("H:mm dd.MM.yyyy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void sixthTestIsValidDate() throws ParseException {
        String date = "21:12 12/06/2023";
        Date correctDate = new SimpleDateFormat("H:mm dd.MM.yyyy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }

    @Test
    public void seventhTestIsValidDate() throws ParseException {
        String date = "21:12.15 12/06/2023";
        Date correctDate = new SimpleDateFormat("H:mm:ss dd.MM.yyyy").parse(date);
        assertEquals(correctDate, validate.isValidDate(date));
    }
}