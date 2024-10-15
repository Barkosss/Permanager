package common;

import common.commands.BaseCommand;
import common.iostream.Input;
import common.iostream.InputTerminal;
import common.iostream.Output;
import common.iostream.OutputTerminal;
import common.utils.JSONHandler;

import org.reflections.Reflections;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Set;
import java.util.*;

public class CommandHandler {
    private static final JSONHandler jsonHandler = new JSONHandler();
    public Map<String, BaseCommand> BaseCommandClasses = new HashMap<>();

    // Запуск команды
    public void getCommand() {
        Input inputTerminal = new InputTerminal();
        Output outputTerminal = new OutputTerminal();

        TelegramBot bot;
        try {
            bot = new TelegramBot(String.valueOf(jsonHandler.read("./src/main/resources/config.json", "tokenTelegram")));
        } catch(Exception err) {
            System.out.println("[ERROR] Telegram authorization: " + err);
            return;
        }

        // Register for updates
        bot.setUpdatesListener(interactions -> {
            Update interaction = interactions.getFirst();
            long chatId = interaction.message().chat().id();

            String message = interaction.message().text();
            try {
                if (message.startsWith("/") && message.charAt(1) != ' ') {
                    String commandName = message.substring(1, (!message.contains(" ")) ? (message.length()) : (message.indexOf(" ")));
                    List<String> args = List.of();
                    if (message.length() != (commandName.length() + 1)) {
                        args = List.of(message.substring(message.indexOf(' ') + 1).split(" "));
                    }
                    BaseCommandClasses.get(commandName).run(args);
                    bot.execute(new SendMessage(chatId, "Is command - " + commandName + " args: " + args));
                } else {
                    bot.execute(new SendMessage(chatId, "Is not command"));
                }
            } catch(Exception err) {
                System.out.println("[ERROR] Telegram something: " + err);
            }

            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Create Exception Handler
            }, err -> System.out.println("[ERROR] Telegram updates listener: " + err)
        );

        while(true) {
            outputTerminal.output("Enter command: ", true);
            List<String> args = inputTerminal.getString(" ");
            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                break;
            }

            if (BaseCommandClasses.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    BaseCommandClasses.get(commandName).run(args);
                } catch(Exception err) {
                    System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("Error: Command \"" + commandName + "\" is not found. ", true);
            }
        }
    }

    // Загрузчик команд
    public void commandLoader() {
        try {
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            BaseCommand instanceClass;
            for (Class<? extends BaseCommand> subclass : subclasses) {
                instanceClass = subclass.getConstructor().newInstance();

                // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                BaseCommandClasses.put(instanceClass.getCommandName(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}