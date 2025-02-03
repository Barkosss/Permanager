package common;

import common.commands.BaseCommand;
import common.iostream.InputConsole;
import common.iostream.InputTelegram;
import common.iostream.OutputHandler;
import common.models.Content;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;
import common.models.User;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.repositories.WarningRepository;
import common.utils.LoggerHandler;
import common.utils.SystemService;
import common.utils.ValidateService;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class CommandHandler {
    LoggerHandler logger = new LoggerHandler();
    ValidateService validate = new ValidateService();

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
    WarningRepository warningRepository = new WarningRepository();

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

                // Если название команды пустое, то пропускаем ход
                if (commandName.isEmpty()) {
                    continue;
                }

                // Проверка, нет ли команд с таким именем в мапе
                if (!baseCommandClasses.containsKey(commandName)) {
                    // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                    baseCommandClasses.put(commandName, instanceClass);
                } else {
                    String errMessage = String.format("There was a duplication of the command - %s", commandName);
                    logger.error(errMessage, true);
                    System.exit(0);
                }
            }

        } catch (Exception err) {
            logger.error(String.format("Command loader: %s", err));
        }
    }

    // Запуск программы
    public void launch(Interaction interaction, LaunchPlatform platform) {
        interaction.setUserRepository(userRepository)
                .setServerRepository(serverRepository)
                .setReminderRepository(reminderRepository)
                .setWarningRepository(warningRepository);

        // Проверка, что Platform это Telegram или ALL
        if (platform == LaunchPlatform.TELEGRAM || platform == LaunchPlatform.ALL) {
            // Поток для Telegram
            Thread threadTelegram = getThreadTelegram(interaction);
            threadTelegram.start();
            logger.info("SYSTEM: Telegram is launch", true);
        }

        new SystemService(interaction);

        // Проверка, что Platform это Console или ALL
        if (platform == LaunchPlatform.CONSOLE || platform == LaunchPlatform.ALL) {
            userRepository.create(0, 0L);
            // Поток для Console
            Thread threadConsole = getThreadConsole();
            threadConsole.start();
            logger.info("SYSTEM: Console is launch", true);
        }

        System.out.println("Program is launch");
    }

    @NotNull
    private Thread getThreadTelegram(Interaction interaction) {
        Thread threadTelegram = new Thread(() ->
                inputTelegram.read(interaction
                                .setUserRepository(userRepository)
                                .setServerRepository(serverRepository)
                                .setReminderRepository(reminderRepository)
                                .setWarningRepository(warningRepository),
                        this)
        );
        threadTelegram.setName("Thread-TELEGRAM-1");
        return threadTelegram;
    }

    @NotNull
    private Thread getThreadConsole() {
        Thread threadConsole = new Thread(() ->
                inputConsole.listener(new InteractionConsole()
                        .setChatId(0)
                        .setUserRepository(userRepository)
                        .setReminderRepository(reminderRepository), this)
        );
        threadConsole.setName("Thread-CONSOLE-1");
        return threadConsole;
    }

    // Вызов команды
    public void launchCommand(Interaction interaction, List<Content> contents) {

        for (Content content : contents) {
            interaction.setContent(content);

            // Если сообщение в Telegram было отправлено во время offline
            if (content.platform() == Interaction.Platform.TELEGRAM
                    && content.createdAt() < ((InteractionTelegram) interaction).timestampBotStart) {
                continue;
            }

            // Если пользователь отсутствует в памяти
            if (!interaction.existsUserById(content.chat().id(), content.userId())) {
                interaction.createUser(content.chat().id(), content.userId());
            }

            String message = content.message();
            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase().substring(1);

            // Берём название команды до "@"
            if (message.startsWith("/") && message.charAt(1) != ' ' && commandName.contains("@")) {
                commandName = commandName.substring(0, commandName.lastIndexOf("@"));
            }

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' '
                    && interaction.getUser(content.userId()).getInputStatus() == User.InputStatus.COMPLETED) {

                if (commandName.startsWith("exit")
                        && (interaction.getPlatform() == Interaction.Platform.CONSOLE
                        || List.of(746875461L, 0L).contains(interaction.getUserId()))) {
                    logger.info("Program is stop", true);
                    System.exit(0);
                }

                interaction.setMessage(message).setArguments(args.subList(1, args.size()))
                        .setLanguageCode(content.language());

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        logger.debug(String.format("Method(run) from command(%s) with Interaction=%s",
                                commandName, interaction));
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        logger.error(String.format("Invoke method (run) in command \"%s\": %s", commandName, err));
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    output.output(interaction.setMessage(String.format("Error: Command \"%s\" is not found.",
                            commandName)).setInline(false));
                }

                // Если что-то ожидаем от пользователя
            } else {
                User user = interaction.getUser(interaction.getUserId());

                if (commandName.startsWith("cancel")) {
                    String commandException = user.getCommandException();
                    user.clearExpected(commandException);
                    output.output(interaction.setMessage(String.format("Command \"%s\" is cancel", commandException))
                            .setInline(false));
                    return;
                }

                // Проверка, ожидаем ли мы что-то от пользователя
                if (interaction.getUser(interaction.getUserId()).getInputStatus() == User.InputStatus.WAITING) {
                    logger.debug(String.format("Get exception value: chatId=%d, userId=%d, message=%s",
                            interaction.getChatId(), interaction.getUserId(), message));

                    if (message.equals("/skip")) {
                        user.setValue(message);
                    } else {
                        InputExpectation.UserInputType inputType = user.getInputType();
                        switch (inputType) {

                            case DATE: { // Проверка на дату
                                Optional<LocalDateTime> validDate = validate.isValidDate(message);
                                Optional<LocalDateTime> validTime = validate.isValidTime(message);

                                if (validDate.isPresent()) {
                                    user.setValue(validDate.get());
                                } else {
                                    validTime.ifPresent(user::setValue);
                                }
                                break;
                            }

                            case INTEGER: { // Проверка на число (Integer)
                                Optional<Integer> validInteger = validate.isValidInteger(message);

                                validInteger.ifPresent(user::setValue);
                                break;
                            }

                            case LONG: { // Проверка на число (Long)
                                Optional<Long> validInteger = validate.isValidLong(message);

                                validInteger.ifPresent(user::setValue);
                                break;
                            }

                            default: { // Строка или любой другой тип
                                user.setValue(message);
                                break;
                            }
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
