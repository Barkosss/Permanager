import common.constructors.Group;
import common.constructors.*;
import org.junit.jupiter.api.Test;

public class ConstructorsTest {

    @Test
    public void testGroup() {
        Permissions permissions = new Permissions();
        Group group = new Group("Moderation", 10, permissions);

    }

    @Test
    public void testMember() {
        // ...
    }

    @Test
    public void testPermissions() {
        // ...
    }

    @Test
    public void testServer() {
        // ...
    }

    @Test
    public void testStatus() {
        // ...
    }

    @Test
    public void testTask() {
        // ...
    }
}



/*
import utils.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
*/