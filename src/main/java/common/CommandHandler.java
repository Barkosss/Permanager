package common;

import common.commands.BaseCommand;
import common.iostream.*;
import common.models.Interaction;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;
import common.models.Content;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {

    // Хэшмап классов команд
    public Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    Input inputConsole = new InputConsole();
    Output output = new OutputHandler();

    // Загрузка команд
    public CommandHandler() {
        try {
            Reflections reflections = new Reflections("common.commands");
            // Получаем множеством всех классов, которые реализовывают интерфейс BaseCommand
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            BaseCommand instanceClass;
            // Проходимся по каждому классу
            for (Class<? extends BaseCommand> subclass : subclasses) {
                // Создаём экземпляр класса
                instanceClass = subclass.getConstructor().newInstance();

                // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                baseCommandClasses.put(instanceClass.getCommandName().toLowerCase(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }

    // ...
    public void run(Interaction interaction, List<Content> updates) {
        // ...
        // ...
        // ...
    }

    // ...
    public void config(Interaction interaction, CommandHandler commandHandler) {
        while(interaction.getPlatform() == null) {
            output.output(interaction);

            String platform = inputConsole.getString(interaction).toLowerCase();
            interaction.setPlatform(Interaction.Platform.valueOf(platform));

            if (interaction.getPlatform() != null) {
                break;
            }
        }
    }

    // Запуск команд в телеграмме
    public void getCommandTelegram(Interaction interaction, List<Content> updates) {
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction.setPlatform(Interaction.Platform.TELEGRAM));

        for(Content update : updates) {

            // Если время отправки сообщения раньше, чем запуск бота (Отправлено во время офлайн)
            if (update.createdAt() <= interactionTelegram.TIMESTAMP_BOT_START) {
                continue;
            }

            // Проверяем, не пустое ли сообщение
            if (update.message() == null) {
                continue;
            }

            // Получаем содержимое сообщения
            String message = update.message();

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' ') {
                long chatId = update.chatId();
                List<String> args = List.of(message.split(" "));
                String commandName = args.getFirst().toLowerCase().substring(1);

                interactionTelegram.setUpdate(update).setArguments(args.subList(1, args.size()));

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {
                    // Если хэшмапа не инициализирована
                    if (interactionTelegram.getUserInputExpectation().getExpectedInput() == null) {
                        // Инициализируем
                        interactionTelegram.getUserInputExpectation().setValue(new HashMap<>());
                    }

                    // Если в хэшмапе не нашли ключ со значением "название команды"
                    if (!interactionTelegram.getUserInputExpectation().getExpectedInput().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interactionTelegram.getUserInputExpectation().getExpectedInput();
                        map.put(commandName, new HashMap<>());
                        interactionTelegram.getUserInputExpectation().setValue(map);
                    }

                    // Запустить класс, в котором будет работать команда
                    try {
                        baseCommandClasses.get(commandName).run(interactionTelegram);

                    } catch (Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interactionTelegram.setUserID(chatId).setMessage("Error: Command \"" + commandName + "\" is not found."));
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interactionTelegram.getUserInputExpectation().getInputKey() != null) {
                    Map<String, Map<String, String>> map = interactionTelegram.getUserInputExpectation().getExpectedInput();
                    map.get(interactionTelegram.getUserInputExpectation().getInputCommandName()).put(interactionTelegram.getUserInputExpectation().getInputKey(), message);
                    interactionTelegram.getUserInputExpectation().setValue(map);

                    baseCommandClasses.get(interactionTelegram.getUserInputExpectation().getInputCommandName()).run(interactionTelegram);
                }
            }
        }
    }

    // Запуск команды
    public void getCommand(InteractionTelegram interactionTelegram, CommandHandler commandHandler) {
        Input input = new InputConsole();
        // Вызываем метод для чтения сообщений из телеграмма
        new InputTelegram().read(interactionTelegram, commandHandler);

        InteractionConsole interactionConsole = new InteractionConsole();

        while(true) {
            // Проверка, ожидаем ли что-то от пользователя
            if (interactionConsole.getUserInputExpectation().getInputKey() == null) {
                output.output(interactionConsole.setMessage("Enter command: ").setInline(true));
            }

            String message = input.getString(interactionConsole).trim();

            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase();

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
            }

            interactionConsole.setMessage(message).setArguments(args.subList(1, args.size()));

            // Если название команды есть в хэшмапе
            if (baseCommandClasses.containsKey(commandName)) {
                // Проверяем, пустой ли хэшмап (для ожидаемых значений)
                if (interactionConsole.getUserInputExpectation().getExpectedInput() == null) {
                    interactionConsole.getUserInputExpectation().setValue(new HashMap<>());
                }

                // Проверка, имеется ли в хэшмапе ключ "название команды"
                if (!interactionConsole.getUserInputExpectation().getExpectedInput().containsKey(commandName)) {
                    Map<String, Map<String, String>> map = interactionConsole.getUserInputExpectation().getExpectedInput();
                    map.put(commandName, new HashMap<>());
                    interactionConsole.getUserInputExpectation().setValue(map);
                }

                // Запустить класс, в котором будет работать команда
                try {
                    baseCommandClasses.get(commandName).run(interactionConsole);

                } catch(Exception err) {
                    System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли ответ от пользователя
                if (interactionConsole.getUserInputExpectation().getInputKey() != null) {
                    Map<String, Map<String, String>> map = interactionConsole.getUserInputExpectation().getExpectedInput();
                    map.get(interactionConsole.getUserInputExpectation().getInputCommandName()).put(interactionConsole.getUserInputExpectation().getInputKey(), message);
                    interactionConsole.getUserInputExpectation().setValue(map);

                    baseCommandClasses.get(interactionConsole.getUserInputExpectation().getInputCommandName()).run(interactionConsole);
                }
            }
        }
    }
}
