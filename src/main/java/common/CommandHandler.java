package common;

import common.commands.BaseCommand;
import common.iostream.*;
import common.models.Interaction;
import common.models.Content;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {

    // Хэшмап классов команд
    public Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    InputTelegram inputTelegram = new InputTelegram();
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

    // Program launch
    public void launch(Interaction interaction) {
        switch(interaction.getPlatform()) {
            case TELEGRAM: {
                System.out.println("Telegram is active");
                inputTelegram.read(interaction, this);
                break;
            }

            case CONSOLE: {
                while(true) {
                    // Проверка, ожидаем ли что-то от пользователя
                    if (interaction.getUserInputExpectation().getInputKey() == null) {
                        output.output(interaction.setMessage("Enter command: ").setInline(true));
                    }

                    String userInputMessage = inputConsole.getString(interaction).trim();

                    // Если команда - выключить бота
                    if (userInputMessage.equals("exit")) {
                        System.out.println("Program is stop");
                        System.exit(0);
                    }
                    launchCommand(interaction, List.of(
                            new Content(userInputMessage,
                                    System.currentTimeMillis() / 1000,
                                    List.of(userInputMessage.split(" "))
                            )
                        )
                    );
                }
            }

            case ALL: {
                System.out.println("All platforms are active");
                inputTelegram.read(interaction, this);
                while(true) {
                    // Проверка, ожидаем ли что-то от пользователя
                    if (interaction.getUserInputExpectation().getInputKey() == null) {
                        output.output(interaction.setMessage("Enter command: ").setInline(true));
                    }

                    String userInputMessage = inputConsole.getString(interaction).trim();

                    // Если команда - выключить бота
                    if (userInputMessage.equals("exit")) {
                        System.out.println("Program is stop");
                        System.exit(0);
                    }

                    launchCommand(interaction, List.of(
                                    new Content(userInputMessage,
                                            System.currentTimeMillis() / 1000,
                                            List.of(userInputMessage.split(" "))
                                    )
                            )
                    );
                }
            }

        }
    }

    // Launch command
    public void launchCommand(Interaction interaction, List<Content> contents) {

        for(Content content : contents) {
            String message = content.message();

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' ') {
                List<String> args = List.of(message.split(" "));
                String commandName = args.getFirst().toLowerCase().substring(1);

                interaction.setMessage(message).setArguments(args.subList(1, args.size()));

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {
                    // Если хэшмапа не инициализирована
                    if (interaction.getUserInputExpectation().getExpectedInput() == null) {
                        // Инициализируем
                        interaction.getUserInputExpectation().setValue(new HashMap<>());
                    }

                    // Если в хэшмапе не нашли ключ со значением "название команды"
                    if (!interaction.getUserInputExpectation().getExpectedInput().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interaction.getUserInputExpectation().getExpectedInput();
                        map.put(commandName, new HashMap<>());
                        interaction.getUserInputExpectation().setValue(map);
                    }

                    // Запустить класс, в котором будет работать команда
                    try {
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setMessage("Error: Command \"" + commandName + "\" is not found."));
                }

                // Если не команда
            } else {

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interaction.getUserInputExpectation().getInputKey() != null) {
                    Map<String, Map<String, String>> map = interaction.getUserInputExpectation().getExpectedInput();
                    map.get(interaction.getUserInputExpectation().getInputCommandName()).put(interaction.getUserInputExpectation().getInputKey(), message);
                    interaction.getUserInputExpectation().setValue(map);

                    baseCommandClasses.get(interaction.getUserInputExpectation().getInputCommandName()).run(interaction);
                }
            }
        }
    }

    // Configuration of interactions and program launch
    public void choosePlatform(Interaction interaction) {
        do {
            interaction.setPlatform(Interaction.Platform.CONSOLE);
            output.output(interaction.setMessage("Choose platform (Console, Telegram, Discord or All): ").setInline(true));

            String platform = inputConsole.getString(interaction).toLowerCase();
            interaction.setPlatform(Interaction.Platform.valueOf(platform.toUpperCase()));

            if (interaction.getPlatform() != null) {
                break;
            }

            output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
        } while(interaction.getPlatform() == null);

        launch(interaction);
    }
}
