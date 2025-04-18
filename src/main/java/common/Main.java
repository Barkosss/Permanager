package common;

import com.pengrad.telegrambot.TelegramBot;
import common.models.Interaction;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;

import java.sql.Timestamp;

public class Main {

    public static void main(String[] args) {
        LoggerHandler logger = new LoggerHandler();

        try {
            logger.newLine();
            JSONHandler jsonHandler = new JSONHandler();
            logger.info("--------".repeat(3) + " BOT IS STARTED " + "--------".repeat(3));

            // Загрузка команд
            logger.debug("Initializing CommandHandler");
            CommandHandler commandHandler = new CommandHandler();

            // Настройка взаимодействий и запуск программы
            logger.debug("Choosing platform");
            CommandHandler.LaunchPlatform platform = commandHandler.choosePlatform(args);

            Interaction interaction = new InteractionConsole();

            if (platform == CommandHandler.LaunchPlatform.TELEGRAM || platform == CommandHandler.LaunchPlatform.ALL) {
                logger.debug("Platform includes TELEGRAM, setting up TelegramBot");
                TelegramBot bot = null;

                try {
                    logger.debug("Checking tokenTelegram in config.json");
                    if (jsonHandler.check("config.json", "tokenTelegram")) {
                        bot = new TelegramBot(String.valueOf(jsonHandler.read("config.json", "tokenTelegram")));
                        logger.info("Telegram bot is start");
                    } else {
                        logger.error("Telegram token isn't found", true);
                        System.exit(404);
                    }

                } catch (Exception err) {
                    logger.error(String.format("Telegram authorization: %s", err), true);
                    System.exit(511);
                }

                // Сохраняем токен бота
                long timestamp = new Timestamp(System.currentTimeMillis() / 1000).getTime();
                logger.debug("Creating InteractionTelegram with timestamp: " + timestamp);
                interaction = new InteractionTelegram(bot, timestamp);
            }

            // Вызываем взаимодействие с нужной платформой
            logger.debug("Launching CommandHandler");
            commandHandler.launch(interaction, platform);

        } catch (Exception err) {
            logger.fatal(String.format("Error with the Main class: %s", err), true);
        }
    }
}
