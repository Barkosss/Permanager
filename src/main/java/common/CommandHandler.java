package common;

import common.commands.BaseCommand;
import common.iostream.*;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;
import common.models.Update;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {

    // Хэшмап классов команд
    public Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    Output output = new OutputHandler();

    // Запуск команд в телеграмме
    public void getCommandTelegram(InteractionTelegram interactionTelegram) {
        InputTelegram input = new InputTelegram();
        List<Update> updates = input.getUpdates(interactionTelegram);

        for(Update update : updates) {
            // Если время отправки сообщения раньше, чем запуск бота (Отправлено во время офлайн)
            if (update.getCreatedAt() <= interactionTelegram.TIMESTAMP_BOT_START) {
                continue;
            }

            // Проверяем, не пустое ли сообщение
            if (update.getMessage() == null) {
                continue;
            }

            // Получаем содержимое сообщения
            String message = update.getMessage();

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' ') {
                long chatId = update.getChatId();
                List<String> args = List.of(message.split(" "));
                String commandName = args.getFirst().toLowerCase().substring(1);

                interactionTelegram.setUpdate(update).setPlatform("telegram").setArguments(args.subList(1, args.size()));

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {
                    // Если хэшмапа не инициализирована
                    if (interactionTelegram.getExpectedInput() == null) {
                        // Инициализируем
                        interactionTelegram.setValue(new HashMap<>());
                    }

                    // Если в хэшмапе не нашли ключ со значением "название команды"
                    if (!interactionTelegram.getExpectedInput().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interactionTelegram.getExpectedInput();
                        map.put(commandName, new HashMap<>());
                        interactionTelegram.setValue(map);
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
                if (interactionTelegram.getInputKey() != null) {
                    Map<String, Map<String, String>> map = interactionTelegram.getExpectedInput();
                    map.get(interactionTelegram.getInputCommandName()).put(interactionTelegram.getInputKey(), message);
                    interactionTelegram.setValue(map);

                    baseCommandClasses.get(interactionTelegram.getInputCommandName()).run(interactionTelegram);
                }
            }
        }
    }

    // Запуск команды
    public void getCommand(InteractionTelegram interactionTelegram) {
        Input input = new InputConsole();
        // Вызываем метод для чтения сообщений из телеграмма
        getCommandTelegram(interactionTelegram);

        InteractionConsole interactionConsole = new InteractionConsole();

        while(true) {
            // Проверка, ожидаем ли что-то от пользователя
            if (interactionConsole.getInputKey() == null) {
                output.output(interactionConsole.setMessage("Enter command: ").setPlatform("terminal").setInline(true));
            }

            String message = input.getString().trim();

            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase();

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
            }

            interactionConsole.setMessage(message).setPlatform("terminal").setArguments(args.subList(1, args.size()));

            // Если название команды есть в хэшмапе
            if (baseCommandClasses.containsKey(commandName)) {
                // Проверяем, пустой ли хэшмап (для ожидаемых значений)
                if (interactionConsole.getExpectedInput() == null) {
                    interactionConsole.setValue(new HashMap<>());
                }

                // Проверка, имеется ли в хэшмапе ключ "название команды"
                if (!interactionConsole.getExpectedInput().containsKey(commandName)) {
                    Map<String, Map<String, String>> map = interactionConsole.getExpectedInput();
                    map.put(commandName, new HashMap<>());
                    interactionConsole.setValue(map);
                }

                // Запустить класс, в котором будет работать команда
                try {
                    baseCommandClasses.get(commandName).run(interactionConsole.setPlatform("terminal"));

                } catch(Exception err) {
                    System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли ответ от пользователя
                if (interactionConsole.getInputKey() != null) {
                    Map<String, Map<String, String>> map = interactionConsole.getExpectedInput();
                    map.get(interactionConsole.getInputCommandName()).put(interactionConsole.getInputKey(), message);
                    interactionConsole.setValue(map);

                    baseCommandClasses.get(interactionConsole.getInputCommandName()).run(interactionConsole);
                }
            }
        }
    }

    // Загрузчик команд
    public void commandLoader() {
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
                baseCommandClasses.put(instanceClass.getCommandName(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}
