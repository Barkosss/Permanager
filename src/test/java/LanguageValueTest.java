import common.exceptions.WrongArgumentsException;
import common.models.Interaction;
import common.models.InteractionConsole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LanguageValueTest {

    @Test
    public void testLanguageValueStringPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.string", List.of("Тестовая строка"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertEquals("Это сообщение \"Тестовая строка\" для проверки метода (Строка)", checkMessage);
    }

    @Test
    public void testLanguageValueStringsPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.strings", List.of("Тестовая строка #1",
                    "Тестовая строка #2",
                    "Тестовая строка #3"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertEquals("Это сообщение \"Тестовая строка #1\" \"Тестовая строка #2\" "
                + "\"Тестовая строка #3\" для проверки метода (Строка)", checkMessage);
    }

    @Test
    public void testLanguageValueIntegerPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.int", List.of("1"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertEquals("Это сообщение \"1\" для проверки метода (Число)", checkMessage);
    }

    @Test
    public void testLanguageValueIntegerNegative() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.int", List.of("1a"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertNotEquals("Это сообщение \"1\" для проверки метода (Число)", checkMessage);
    }

    @Test
    public void testLanguageValueLocalDatePositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.date", List.of("29.11.2024 12:50"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertEquals("Это сообщение \"29.11.2024 12:50\" для проверки метода (Дата)", checkMessage);
    }

    @Test
    public void testLanguageValueLocalDateNegative() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage;
        try {
            checkMessage = interaction.getLanguageValue("test.date", List.of("Local Date"));
        } catch (WrongArgumentsException err) {
            checkMessage = "Undefined";
        }

        assertNotEquals("Это сообщение \"29.11.2024 12:50\" для проверки метода (Дата)", checkMessage);
    }
}
