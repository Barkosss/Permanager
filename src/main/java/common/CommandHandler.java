package common;

import common.commands.BaseCommand;
import common.iostream.InputConsole;
import common.iostream.InputTelegram;
import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Content;
import common.models.Interaction;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;
import common.models.User;
import common.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class CommandHandler {

    public enum launchPlatform {
        TELEGRAM,
        CONSOLE,
        ALL
    }

    // Хэшмап классов команд
    Map<String, BaseCommand> baseCommandClasses = new HashMap<>();
    UserRepository userRepository = new UserRepository();
    InputTelegram inputTelegram = new InputTelegram();
    InputConsole inputConsole = new InputConsole();
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
    public void launch(Interaction interaction, launchPlatform platform) {

        // Проверка, что Platform это Telegram или ALL
        if (platform == launchPlatform.TELEGRAM || platform == launchPlatform.ALL) {
            System.out.println("Telegram is launch");
            // Поток для Telegram
            new Thread(() ->
                    inputTelegram.read(interaction.setUserRepository(userRepository), this)
            ).start();
        }

        // Проверка, что Platform это Console или ALL
        if (platform == launchPlatform.CONSOLE || platform == launchPlatform.ALL) {
            userRepository.create(0L);
            System.out.println("Console is launch");
            // Поток для Console
            new Thread(() ->
                    inputConsole.listener(new InteractionConsole().setUserRepository(userRepository), this)
            ).start();
        }
    }

    // Вызов команды
    public void launchCommand(Interaction interaction, List<Content> contents) {

        for (Content content : contents) {
            String message = content.message();

            // Если сообщение в Telegram было отправлено во время offline
            if (content.platform() == Interaction.Platform.TELEGRAM &&
                    content.createdAt() < ((InteractionTelegram) interaction).timestampBotStart) {
                continue;
            }

            // сли пользователь отсутствует в памяти
            if (!userRepository.existsById(content.userId())) {
                userRepository.create(content.userId());
            }

            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase().substring(1);

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' ' && interaction.getUser(interaction.getUserID()).getInputStatus() == User.InputStatus.COMPLETED) {

                if (commandName.startsWith("exit") && interaction.getPlatform() == Interaction.Platform.CONSOLE) {
                    System.out.println("Program is stop");
                    System.exit(0);
                }

                interaction.setMessage(message).setArguments(args.subList(1, args.size()));

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        System.out.println("[ERROR] Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setMessage("Error: Command \"" + commandName + "\" is not found.").setInline(false));
                }

                // Если что-то ожидаем от пользователя
            } else {

                if (commandName.startsWith("cancel")) {
                    User user = interaction.getUser(interaction.getUserID());
                    String commandException = user.getCommandException();
                    user.clearExpected(commandException);
                    output.output(interaction.setMessage("Command \"" + commandException + "\" is cancel").setInline(false));
                    return;
                }

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interaction.getUser(interaction.getUserID()).getInputStatus() == User.InputStatus.WAITING) {
                    interaction.getUser(interaction.getUserID()).setValue(message);
                    baseCommandClasses.get(interaction.getUser(interaction.getUserID()).getCommandException()).run(interaction);
                }
            }
        }
    }

    // Настройка взаимодействий и запуск программы
    public launchPlatform choosePlatform(String[] args) {
        Interaction interaction = new InteractionConsole();

        String userPlatform;
        do {
            if (List.of("console", "telegram", "all").contains(args[0].toLowerCase())) {
                userPlatform = args[0];
            } else {
                output.output(interaction.setMessage("Choose platform (Console, Telegram or All): ").setInline(true));
                // Получаем платформу от пользователя, с консоли
                userPlatform = inputConsole.getString().toLowerCase();
            }


            try {
                // Пытаемся получить платформу
                return launchPlatform.valueOf(userPlatform.toUpperCase());

                // Ошибка, если указан неправильная платформа
            } catch (IllegalArgumentException err) {
                output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
            }

        } while (true);
    }
}