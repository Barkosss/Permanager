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
    public Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    Input input = new InputHandler();
    Output output = new OutputHandler();

    public void getCommandTelegram(Interaction interaction) {

        // Register for updates
        interaction.TELEGRAM_BOT.setUpdatesListener(updates -> {
            Update update = updates.getFirst();

            if (update.message() == null || update.message().text() == null) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }

            String message = update.message().text();

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' ') {
                long chatId = update.message().chat().id();
                List<String> args = List.of(message.split(" "));
                String commandName = args.getFirst().toLowerCase();
                args = args.subList(1, args.size());
                interaction.setUserID(chatId).setMessage(message).setPlatform("telegram").setArguments(args);
                commandName = commandName.substring(1);
                if (baseCommandClasses.containsKey(commandName)) {
                    if (interaction.getCommandStatus() == null) {
                        interaction.setCommandStatus(new HashMap<>());
                    }

                    if (!interaction.getCommandStatus().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interaction.getCommandStatus();
                        map.put(commandName, new HashMap<>());
                        interaction.setCommandStatus(map);
                    }

                    // Запустить класс, в котором будет работать команда
                    try {
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch(Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setUserID(chatId).setMessage("Error: Command \"" + commandName + "\" is not found."));
                }

            } else {
                if (interaction.getCommandStatusKey() != null) {
                    Map<String, Map<String, String>> map = interaction.getCommandStatus();
                    map.get(interaction.getCommandStatusName()).put(interaction.getCommandStatusKey(), message);
                    interaction.setCommandStatus(map);

                    baseCommandClasses.get(interaction.getCommandStatusName()).run(interaction);
                }
            }

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));
    }

    // Запуск команды
    public void getCommand(Interaction interaction) {
        getCommandTelegram(interaction);
        // Вызываем метод для чтения сообщений из телеграмма

        while(true) {
            output.output(interaction.setMessage("Enter command: ").setPlatform("terminal").setInline(true));
            String message = input.getString(interaction);
            List<String> args = List.of(message.split(" "));
            interaction.setMessage(message).setPlatform("terminal");

            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());
            interaction.setArguments(args);

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
            }

            if (baseCommandClasses.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    baseCommandClasses.get(commandName).run(interaction);

                } catch(Exception err) {
                    System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                output.output(interaction.setMessage("Error: Command \"" + commandName + "\" is not found. ").setInline(true));
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
                baseCommandClasses.put(instanceClass.getCommandName(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}