package common;

import com.pengrad.telegrambot.TelegramBot;

import java.sql.Timestamp;

import common.models.Interaction;
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
        // Saving bot's token
        InteractionTelegram interactionTelegram = new InteractionTelegram(bot, new Timestamp(System.currentTimeMillis() / 1000).getTime());
        // Loader commands
        CommandHandler commandHandler = new CommandHandler();
        // Configuration of interactions and program launch
        commandHandler.choosePlatform(interactionTelegram);
    }
}