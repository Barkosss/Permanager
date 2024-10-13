package common;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import common.commands.BaseCommand;
import common.iostream.Input;
import common.iostream.InputTerminal;
import common.iostream.Output;
import common.iostream.OutputTerminal;
import common.utils.JSONHandler;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {
    private static final JSONHandler jsonHandler = new JSONHandler();

    public Map<String, BaseCommand> BaseCommandClasses = new HashMap<>();

    // Запуск команды
    public void getCommand() {
        Input inputTerminal = new InputTerminal();
        Output outputTerminal = new OutputTerminal();

        TelegramBot bot = new TelegramBot(jsonHandler.read("./src/main/resources/config.json", "tokenTelegram").toString());

        // Register for updates
        bot.setUpdatesListener(updates -> {
            Update update = updates.getFirst();
            long chatId = update.message().chat().id();
            if (update.message().text().startsWith("/")) {
                SendResponse response = bot.execute(new SendMessage(chatId, "Is command"));
                String commandName = update.message().text().substring(1, update.message().text().indexOf(" "));
                List<String> args = List.of(update.message().text().substring(update.message().text().indexOf(" ")).split(" "));
                BaseCommandClasses.get(commandName).run(args);
            } else {
                BaseCommandClasses.get("help").run(null);
            }

            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
            // Create Exception Handler
            }, err -> System.out.println("[ERROR] Telegram command handler: " + err)
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
                    System.out.println("[ERROR] Something error: " + err);
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