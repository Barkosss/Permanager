package common;

import com.pengrad.telegrambot.TelegramException;
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

    // Запуск программы
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
                    if (interaction.getUserInputExpectation().getExpectedInputKey() == null) {
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

            case ALL: { // TODO: Подумать над переработкой или надобностью. Всё работает только в одной платформе
                System.out.println("All platforms are active");
                Thread telegramThread = new Thread(() -> {
                    try {
                        inputTelegram.read(interaction.setPlatform(Interaction.Platform.TELEGRAM), this);
                    } catch(TelegramException err) {
                        System.out.println("[ERROR] Telegram thread:" + err);
                    }
                });

                while(true) {
                    interaction.setPlatform(Interaction.Platform.CONSOLE);

                    // Проверка, ожидаем ли что-то от пользователя
                    if (interaction.getUserInputExpectation().getExpectedInputKey() == null) {
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

    // Вызов команды
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
                    if (interaction.getUserInputExpectation().getExpectedInputs() == null) {
                        // Инициализируем
                        interaction.getUserInputExpectation().setValue(new HashMap<>());
                    }

                    // Если в хэшмапе не нашли ключ со значением "название команды"
                    if (!interaction.getUserInputExpectation().getExpectedInputs().containsKey(commandName)) {
                        Map<String, Map<String, String>> map = interaction.getUserInputExpectation().getExpectedInputs();
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
                if (interaction.getUserInputExpectation().getExpectedInputKey() != null) {
                    Map<String, Map<String, String>> map = interaction.getUserInputExpectation().getExpectedInputs();
                    map.get(interaction.getUserInputExpectation().getExpectedCommandName()).put(interaction.getUserInputExpectation().getExpectedInputKey(), message);
                    interaction.getUserInputExpectation().setValue(map);

                    baseCommandClasses.get(interaction.getUserInputExpectation().getExpectedCommandName()).run(interaction);
                }
            }
        }
    }

    // Настройка взаимодействий и запуск программы
    public void choosePlatform(Interaction interaction) {
        do {
            output.output(interaction.setPlatform(Interaction.Platform.CONSOLE).setMessage("Choose platform (Console, Telegram or All): ").setInline(true));

            String platform = inputConsole.getString(interaction).toLowerCase();

            try {
                interaction.setPlatform(Interaction.Platform.valueOf(platform.toUpperCase()));
                break;

            } catch(IllegalArgumentException err) {
                output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
            }

        } while(true);

        launch(interaction);
    }
}
