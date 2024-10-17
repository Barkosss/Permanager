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
    Input input = new InputHandler();
    Output output = new OutputHandler();
    public Map<String, BaseCommand> BaseCommandClasses = new HashMap<>();

    private void getCommandTelegram(Interaction interaction) {

        // Register for updates
        interaction.TELEGRAM_BOT.setUpdatesListener(updates -> {
            Update update = updates.getFirst();

            if (update.message() == null) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }

            String message = update.message().text();
            long chatId = update.message().chat().id();
            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());
            interaction.setUserID(chatId).setMessage(message).setPlatform("telegram").setArguments(args);

            // Проверка, что это команда
            if (commandName.startsWith("/") && message.charAt(1) != ' ') {
                commandName = commandName.substring(1);
                if (BaseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        //outputTelegram.output(new Interaction(chatId, "Complete: Command \"" + commandName + "\" is found. Arguments: " + args));
                        BaseCommandClasses.get(commandName).run(interaction);

                    } catch(Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setUserID(chatId).setMessage("Error: Command \"" + commandName + "\" is not found."));
                }

            } else {
                // Ошибка: Не команда
                output.output(interaction.setMessage("Error: \"" + commandName + "\" is not command."));
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

            if (BaseCommandClasses.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    BaseCommandClasses.get(commandName).run(interaction);

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
                BaseCommandClasses.put(instanceClass.getCommandName(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}
