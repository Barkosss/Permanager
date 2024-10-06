package common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // На данный момент не работает. Потихоньку исправляем и реализовываем работу логов
        logger.info("This is an info message");

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commandLoader(); // Загружаем все команды
        commandHandler.getCommand(); // Вызываем команды
    }
}