import common.utils.Validate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateTest {
    public static Validate validate = new Validate();

    @Test
    public void testIsValidInteger() {
        assertTrue(validate.isValidInteger("33"));
    }

    @Test
    public void testIsValidDate() {
        assertTrue(validate.isValidDate("23:22 10.09.2022"));
    }
}