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
        JSONHandler jsonHandler = new JSONHandler();
        logger.debug("----------------");

        // Загрузка команд
        CommandHandler commandHandler = new CommandHandler();

        // Настройка взаимодействий и запуск программы
        CommandHandler.LaunchPlatform platform = commandHandler.choosePlatform(args);
        Interaction interaction = new InteractionConsole();

        if (platform == CommandHandler.LaunchPlatform.TELEGRAM || platform == CommandHandler.LaunchPlatform.ALL) {
            TelegramBot bot = null;
            try {
                bot = new TelegramBot(String.valueOf(jsonHandler.read("config.json", "tokenTelegram")));
                logger.info("Telegram bot is start");

            } catch (Exception err) {
                logger.error("Telegram authorization: " + err);
                System.exit(511);
            }

            // Сохраняем токен бота
            interaction = new InteractionTelegram(bot, new Timestamp(System.currentTimeMillis() / 1000).getTime());
        }

        // Вызываем взаимодействие с нужной платформой
        commandHandler.launch(interaction, platform);
    }
}
