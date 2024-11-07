package common;

import com.pengrad.telegrambot.TelegramBot;

import java.sql.Timestamp;

import common.models.InteractionTelegram;
import common.utils.JSONHandler;

public class Main {

    public static void main(String[] args) {
        JSONHandler jsonHandler = new JSONHandler();
        TelegramBot bot = null;
        try {
            bot = new TelegramBot(String.valueOf(jsonHandler.read("./src/main/resources/config.json", "tokenTelegram")));

        } catch(Exception err) {
            System.out.println("[ERROR] Telegram authorization: " + err);
            System.exit(511);
        }
        // Сохраняем токен бота
        InteractionTelegram interactionTelegram = new InteractionTelegram(bot, new Timestamp(System.currentTimeMillis() / 1000).getTime());
        // Загрузка команд
        CommandHandler commandHandler = new CommandHandler();
        // Настройка взаимодействий и запуск программы
        commandHandler.choosePlatform(interactionTelegram);
    }
}
