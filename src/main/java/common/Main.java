package common;

import common.utils.JSONHandler;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    public static void main(String[] args) {
        try {
            JSONHandler jsonHandler = new JSONHandler();
            String botToken = (String)jsonHandler.read("./src/main/resources/config.json", "tokenTelegram");
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, new TelegramHandler());
        } catch (TelegramApiException err) {
            System.out.println("[ERROR] Authentication Telegram Bot:" + err);
        }

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commandLoader(); // Загружаем все команды
        commandHandler.getCommand(); // Вызываем команды
    }
}