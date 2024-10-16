package common;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import common.commands.BaseCommand;
import common.iostream.*;
import common.models.Interaction;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {
    public Map<String, BaseCommand> BaseCommandClasses = new HashMap<>();

    private void getCommandTelegram() {
        Output outputTelegram = new OutputTelegram();

        // Register for updates
        Interaction.TELEGRAM_BOT.setUpdatesListener(interactions -> {
            Update interaction = interactions.getFirst();

            if (interaction.message() == null) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }

            String message = interaction.message().text();
            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());

            // Проверка, что это команда
            if (commandName.startsWith("/") && message.charAt(1) != ' ') {
                commandName = commandName.substring(1);

                if (BaseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        outputTelegram.output(new Interaction(interaction.message().chat().id(), "Complete: Command \"" + commandName + "\" is found. Arguments: " + args), false);
                        // BaseCommandClasses.get(commandName).run(args);

                    } catch(Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    outputTelegram.output(new Interaction(interaction.message().chat().id(), "Error: Command \"" + commandName + "\" is not found."), false);
                }

            } else {
                // Ошибка: Не команда
                outputTelegram.output(new Interaction(interaction.message().chat().id(), "Error: \"" + commandName + "\" is not command."), false);
            }

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));
    }

    // Запуск команды
    public void getCommand() {
        getCommandTelegram();
        // Вызываем метод для чтения сообщений из телеграмма

        Input inputTerminal = new InputTerminal();
        Output outputTerminal = new OutputTerminal();

        while(true) {
            outputTerminal.output(new Interaction("Enter command: "), true);
            List<String> args = inputTerminal.getString(" ");
            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
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
                outputTerminal.output(new Interaction("Error: Command \"" + commandName + "\" is not found. "), true);
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