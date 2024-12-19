import common.models.Interaction;
import common.models.InteractionConsole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LanguageValueTest {

    @Test
    public void testLanguageValueStringPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.string", List.of("Тестовая строка"));

        assertEquals("Это сообщение \"Тестовая строка\" для проверки метода (Строка)", checkMessage);
    }

    @Test
    @DisplayName("Проверка на переменные в несколько слов")
    public void testLanguageValueLongStringPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.longString", List.of("Проверка длинных слов"));

        assertEquals("Это сообщение \"Проверка длинных слов\" для проверки поиска длинных (Строка)", checkMessage);
    }

    @Test
    public void testLanguageValueStringsPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.strings", List.of("Тестовая строка #1",
                "Тестовая строка #2",
                "Тестовая строка #3"));

        assertEquals("Это сообщение \"Тестовая строка #1\" \"Тестовая строка #2\" "
                + "\"Тестовая строка #3\" для проверки метода (Строка)", checkMessage);
    }

    @Test
    public void testLanguageValueIntegerPositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.int", List.of("1"));

        assertEquals("Это сообщение \"1\" для проверки метода (Число)", checkMessage);
    }

    @Test
    public void testLanguageValueIntegerNegative() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.int", List.of("1a"));

        assertNotEquals("Это сообщение \"1\" для проверки метода (Число)", checkMessage);
    }

    @Test
    public void testLanguageValueLocalDatePositive() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.date", List.of("29.11.2024 12:50"));

        assertEquals("Это сообщение \"29.11.2024 12:50\" для проверки метода (Дата)", checkMessage);
    }

    @Test
    public void testLanguageValueLocalDateNegative() {
        InteractionConsole interaction = new InteractionConsole();
        interaction.setLanguageCode(Interaction.Language.RUSSIAN);
        String checkMessage = interaction.getLanguageValue("test.date", List.of("Local Date"));

        assertNotEquals("Это сообщение \"29.11.2024 12:50\" для проверки метода (Дата)", checkMessage);
    }
}
