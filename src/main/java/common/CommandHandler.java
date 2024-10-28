package common;

import common.commands.BaseCommand;
import common.iostream.*;
import common.models.Interaction;
import common.models.Update;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {
    // Хэшмап классов команд
    public Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    Output output = new OutputHandler();

    // Запуск команд в телеграмме
    public void getCommandTelegram(Interaction interaction) {
        InputTelegram input = new InputTelegram();
        List<Update> updates = input.getUpdates(interaction);

        for(Update update : updates) {
            // Если время отправки сообщения раньше, чем запуск бота (Отправлено во время офлайн)
            if (update.getCreatedAt() <= interaction.TIMESTAMP_BOT_START) {
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

                interaction.setUpdate(update).setPlatform("telegram").setArguments(args.subList(1, args.size()));

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {
                    // Если хэшмапа не инициализирована
                    if (interaction.getExpectedInput() == null) {
                        // Инициализируем
                        interaction.setValue(new HashMap<>());
                    }

                    // Если в хэшмапе не нашли ключ со значением "название команды"
                    if (!interaction.getExpectedInput().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interaction.getExpectedInput();
                        map.put(commandName, new HashMap<>());
                        interaction.setValue(map);
                    }

                    // Запустить класс, в котором будет работать команда
                    try {
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    //throw new CommandNotFoundException();
                    //output.output(interaction.setUserID(chatId).setMessage("Error: Command \"" + commandName + "\" is not found."));
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interaction.getInputKey() != null) {
                    Map<String, Map<String, String>> map = interaction.getExpectedInput();
                    map.get(interaction.getInputCommandName()).put(interaction.getInputKey(), message);
                    interaction.setValue(map);

                    baseCommandClasses.get(interaction.getInputCommandName()).run(interaction);
                }
            }
        }
    }

    // Запуск команды
    public void getCommand(Interaction interaction) {
        Input input = new InputConsole();
        // Вызываем метод для чтения сообщений из телеграмма
        getCommandTelegram(interaction);

        while(true) {
            // Проверка, ожидаем ли что-то от пользователя
            if (interaction.getInputKey() == null) {
                output.output(interaction.setMessage("Enter command: ").setPlatform("terminal").setInline(true));
            }

            String message = input.getString(interaction).trim();

            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase();

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                System.exit(0);
            }

            interaction.setMessage(message).setPlatform("terminal").setArguments(args.subList(1, args.size()));

            // Если название команды есть в хэшмапе
            if (baseCommandClasses.containsKey(commandName)) {
                // Проверяем, пустой ли хэшмап (для ожидаемых значений)
                if (interaction.getExpectedInput() == null) {
                    interaction.setValue(new HashMap<>());
                }

                // Проверка, имеется ли в хэшмапе ключ "название команды"
                if (!interaction.getExpectedInput().containsKey(commandName)) {
                    Map<String, Map<String, String>> map = interaction.getExpectedInput();
                    map.put(commandName, new HashMap<>());
                    interaction.setValue(map);
                }

                // Запустить класс, в котором будет работать команда
                try {
                    baseCommandClasses.get(commandName).run(interaction.setPlatform("terminal"));

                } catch(Exception err) {
                    System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли ответ от пользователя
                if (interaction.getInputKey() != null) {
                    Map<String, Map<String, String>> map = interaction.getExpectedInput();
                    map.get(interaction.getInputCommandName()).put(interaction.getInputKey(), message);
                    interaction.setValue(map);

                    baseCommandClasses.get(interaction.getInputCommandName()).run(interaction);
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
