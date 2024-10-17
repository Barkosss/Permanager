package common;

import com.pengrad.telegrambot.TelegramBot;

import common.models.Interaction;
import common.utils.JSONHandler;

public class Main {

    public static void main(String[] args) {
        JSONHandler jsonHandler = new JSONHandler();
        TelegramBot bot = null;
        try {
            bot = new TelegramBot(String.valueOf(jsonHandler.read("./src/main/resources/config.json", "tokenTelegram")));

        } catch(Exception err) {
            System.out.println("[ERROR] Telegram authorization: " + err);
            System.exit(404);
        }
        Interaction interaction = new Interaction(bot); // Сохраняем токен бота

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commandLoader(); // Загружаем все команды
        commandHandler.getCommand(interaction); // Вызываем команды из терминала
    }
}