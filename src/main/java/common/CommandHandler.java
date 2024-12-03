package common;

import common.commands.BaseCommand;
import common.iostream.InputConsole;
import common.iostream.InputTelegram;
import common.iostream.OutputHandler;
import common.models.*;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.utils.LoggerHandler;
import common.utils.ReminderHandler;
import common.utils.Validate;
import org.reflections.Reflections;

import java.time.LocalDate;
import java.util.*;

public class CommandHandler {
    LoggerHandler logger = new LoggerHandler();
    Validate validate = new Validate();

    public enum LaunchPlatform {
        TELEGRAM,
        CONSOLE,
        ALL
    }

    // Хэшмап классов команд
    Map<String, BaseCommand> baseCommandClasses = new HashMap<>();

    // Репозитории
    UserRepository userRepository = new UserRepository();
    ServerRepository serverRepository = new ServerRepository();
    ReminderRepository reminderRepository = new ReminderRepository();

    InputTelegram inputTelegram = new InputTelegram();
    InputConsole inputConsole = new InputConsole();
    OutputHandler output = new OutputHandler();

    // Загрузка команд
    public CommandHandler() {
        try {
            Reflections reflections = new Reflections("common.commands");
            // Получаем множеством всех классов, которые реализовывают интерфейс BaseCommand
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            String commandName;
            BaseCommand instanceClass;
            // Проходимся по каждому классу
            for (Class<? extends BaseCommand> subclass : subclasses) {
                // Создаём экземпляр класса
                instanceClass = subclass.getConstructor().newInstance();

                commandName = instanceClass.getCommandName().toLowerCase();
                // Проверка, нет ли команд с таким именем в мапе
                if (!baseCommandClasses.containsKey(commandName)) {
                    // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                    baseCommandClasses.put(commandName, instanceClass);
                } else {
                    logger.error("There was a duplication of the command - " + commandName);
                    System.out.println("There was a duplication of the command - " + commandName);
                    System.exit(0);
                }
            }

        } catch (Exception err) {
            logger.error("Command loader: " + err);
        }
    }

    // Запуск программы
    public void launch(Interaction interaction, LaunchPlatform platform) {

        // Проверка, что Platform это Telegram или ALL
        if (platform == LaunchPlatform.TELEGRAM || platform == LaunchPlatform.ALL) {
            // Поток для Telegram
            Thread threadTelegram = new Thread(() ->
                    inputTelegram.read(interaction.setUserRepository(userRepository)
                            .setServerRepository(serverRepository).setReminderRepository(reminderRepository), this)
            );
            threadTelegram.setName("Thread-" + interaction.getPlatform().name() + "-1");
            threadTelegram.start();
            logger.info("Telegram is launch");
            System.out.println("SYSTEM: Telegram is launch");
        }

        // Поток для системы напоминаний
        Thread threadReminder = new Thread(() ->
                new ReminderHandler().run(interaction)
        );
        threadReminder.setName("Thread-Reminder");
        threadReminder.start();
        System.out.println("SYSTEM: ReminderHandler is launch");

        // Проверка, что Platform это Console или ALL
        if (platform == LaunchPlatform.CONSOLE || platform == LaunchPlatform.ALL) {
            userRepository.create(0L);
            // Поток для Console
            Thread threadConsole = new Thread(() ->
                    inputConsole.listener(new InteractionConsole().setUserRepository(userRepository)
                            .setServerRepository(serverRepository).setReminderRepository(reminderRepository), this)
            );
            threadConsole.setName("Thread-" + interaction.getPlatform().name() + "-2");
            threadConsole.start();
            logger.info("Console is launch");
            System.out.println("SYSTEM: Console is launch");
        }
    }

    // Вызов команды
    public void launchCommand(Interaction interaction, List<Content> contents) {

        for (Content content : contents) {
            interaction.setContent(content);
            String message = content.message();

            // Если сообщение в Telegram было отправлено во время offline
            if (content.platform() == Interaction.Platform.TELEGRAM
                    && content.createdAt() < ((InteractionTelegram) interaction).timestampBotStart) {
                continue;
            }

            // сли пользователь отсутствует в памяти
            if (!userRepository.existsById(content.userId())) {
                userRepository.create(content.userId());
            }

            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase().substring(1);

            // Берём название команды до "@"
            if (message.startsWith("/") && message.charAt(1) != ' ' && commandName.contains("@")) {
                commandName = commandName.substring(0, commandName.lastIndexOf("@"));
            }

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' '
                    && interaction.getUser(interaction.getUserId()).getInputStatus() == User.InputStatus.COMPLETED) {

                if (commandName.startsWith("exit")
                        && (interaction.getPlatform() == Interaction.Platform.CONSOLE
                        || List.of(746875461L, 0L).contains(interaction.getUserId()))) {
                    output.output(interaction.setMessage("Program is stop"));
                    logger.info("Program is stop");
                    System.exit(0);
                }

                interaction.setMessage(message).setArguments(args.subList(1, args.size()))
                        .setLanguageCode(content.language());

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        logger.debug("Method(run) from command(" + commandName + ") with Interaction=" + interaction);
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        logger.error("Invoke method (run) in command \"" + commandName + "\": " + err);
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setMessage("Error: Command \"" + commandName + "\" is not found.")
                            .setInline(false));
                }

                // Если что-то ожидаем от пользователя
            } else {
                User user = interaction.getUser(interaction.getUserId());

                if (commandName.startsWith("cancel")) {
                    String commandException = user.getCommandException();
                    user.clearExpected(commandException);
                    output.output(interaction.setMessage("Command \"" + commandException + "\" is cancel")
                            .setInline(false));
                    return;
                }

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interaction.getUser(interaction.getUserId()).getInputStatus() == User.InputStatus.WAITING) {
                    logger.debug("Get exception value: chatId" + interaction.getChatId()
                            + ", userId=" + interaction.getUserId()
                            + ", message=" + message);

                    InputExpectation.UserInputType inputType = user.getInputType();
                    switch (inputType) {
                        case DATE: { // Проверка на дату
                            Optional<LocalDate> validDate = validate.isValidDate(message);
                            Optional<LocalDate> validTime = validate.isValidTime(message);

                            if (validDate.isPresent()) {
                                user.setValue(validDate.get());
                            } else if (validTime.isPresent()) {
                                user.setValue(validTime.get());
                            } else {
                                output.output(interaction.setMessage("Try again. Enter date").setInline(true));
                            }
                        }
                        case INTEGER: { // Проверка на число
                            Optional<Integer> validInteger = validate.isValidInteger(message);

                            if (validInteger.isPresent()) {
                                user.setValue(validInteger.get());
                            } else {
                                output.output(interaction.setMessage("Try again. Enter number").setInline(true));
                            }
                        }
                        default: { // Строка или любой другой тип
                            user.setValue(message);
                        }
                    }
                    baseCommandClasses.get(interaction.getUser(interaction.getUserId()).getCommandException())
                            .run(interaction);
                }
            }
        }
    }

    // Настройка взаимодействий и запуск программы
    public LaunchPlatform choosePlatform(String[] args) {
        Interaction interaction = new InteractionConsole();

        String userPlatform;
        do {
            if (args.length > 0 && List.of("console", "telegram", "all").contains(args[0].toLowerCase())) {
                userPlatform = args[0];
            } else {
                output.output(interaction.setMessage("Choose platform (Console, Telegram or All): ").setInline(true));
                // Получаем платформу от пользователя, с консоли
                userPlatform = inputConsole.getString().toLowerCase();
            }


            try {
                // Пытаемся получить платформу
                return LaunchPlatform.valueOf(userPlatform.toUpperCase());

                // Ошибка, если указан неправильная платформа
            } catch (IllegalArgumentException err) {
                output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
            }

        } while (true);
    }
}
