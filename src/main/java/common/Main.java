package common;

import common.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // На данный момент не работает. Потихоньку исправляем и реализовываем работу логов
        logger.info("This is an info message");

        CommandHandlerTerminal commandHandlerTerminal = new CommandHandlerTerminal();
        commandHandlerTerminal.commandLoader(); // Загружаем все команды
        commandHandlerTerminal.getCommand(); // Вызываем команды
    }
}