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
import common.repositories.CommandRepository;
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
    CommandRepository commandRepository;

    InputTelegram inputTelegram = new InputTelegram();
    InputConsole inputConsole = new InputConsole();
    OutputHandler output = new OutputHandler();

    // Загрузка команд
    public CommandHandler() {
        logger.debug("Initializing CommandHandler");

        try {
            Reflections reflections = new Reflections("common.commands");
            // Получаем множеством всех классов, которые реализовывают интерфейс BaseCommand
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);
            logger.debug("Found " + subclasses.size() + " command classes");

            String commandName;
            BaseCommand instanceClass;
            // Проходимся по каждому классу
            for (Class<? extends BaseCommand> subclass : subclasses) {
                // Создаём экземпляр класса
                instanceClass = subclass.getConstructor().newInstance();

                commandName = instanceClass.getCommandName().toLowerCase();
                logger.debug("Loading command: " + commandName);

                // Если название команды пустое, то пропускаем ход
                if (commandName.isEmpty()) {
                    logger.debug("Skipped command with empty name: " + subclass.getSimpleName());
                    continue;
                }

                // Проверка, нет ли команд с таким именем в мапе
                if (!baseCommandClasses.containsKey(commandName)) {
                    // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                    baseCommandClasses.put(commandName, instanceClass);
                    logger.debug("Command registered: " + commandName);
                } else {
                    String errMessage = String.format("There was a duplication of the command - %s", commandName);
                    logger.error(errMessage, true);
                    System.exit(0);
                }
            }

            commandRepository = new CommandRepository(baseCommandClasses);
            logger.debug("CommandRepository initialized successfully");

        } catch (Exception err) {
            logger.error(String.format("Command loader: %s", err));
        }
    }

    // Запуск программы
    public void launch(Interaction interaction, LaunchPlatform platform) {
        logger.debug("Launching application with platform: " + platform);

        interaction.setCommandRepository(commandRepository)
                .setUserRepository(userRepository)
                .setServerRepository(serverRepository)
                .setReminderRepository(reminderRepository)
                .setWarningRepository(warningRepository);

        // Проверка, что Platform это Telegram или ALL
        if (platform == LaunchPlatform.TELEGRAM || platform == LaunchPlatform.ALL) {
            logger.debug("Starting Telegram thread");
            // Поток для Telegram
            Thread threadTelegram = getThreadTelegram(interaction);
            threadTelegram.start();
            logger.info("SYSTEM: Telegram is launch", true);
        }

        new SystemService(interaction);

        // Проверка, что Platform это Console или ALL
        if (platform == LaunchPlatform.CONSOLE || platform == LaunchPlatform.ALL) {
            logger.debug("Creating default console user");
            userRepository.create(0, 0L);
            // Поток для Console
            logger.debug("Starting Console thread");
            Thread threadConsole = getThreadConsole();
            threadConsole.start();
            logger.info("SYSTEM: Console is launch", true);
        }

        System.out.println("Program is launch");
    }

    @NotNull
    private Thread getThreadTelegram(Interaction interaction) {
        logger.debug("Creating Telegram thread object");
        Thread threadTelegram = new Thread(() ->
                inputTelegram.read(interaction
                                .setCommandRepository(commandRepository)
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
        logger.debug("Creating Console thread object");
        Thread threadConsole = new Thread(() ->
                inputConsole.listener(new InteractionConsole()
                        .setChatId(0)
                        .setCommandRepository(commandRepository)
                        .setUserRepository(userRepository)
                        .setReminderRepository(reminderRepository), this)
        );
        threadConsole.setName("Thread-CONSOLE-1");
        return threadConsole;
    }

    // Вызов команды
    public void launchCommand(Interaction interaction, List<Content> contents) {

        logger.debug(String.format("Processing %s updates", contents.size()));
        for (Content content : contents) {
            interaction.setContent(content);
            logger.debug("Processing content: " + content);

            // Если сообщение в Telegram было отправлено во время offline
            long deltaSeconds = 30;
            if (content.platform() == Interaction.Platform.TELEGRAM
                    && (content.createdAt() <= ((InteractionTelegram) interaction).getTimestampBotStart() - deltaSeconds)) {
                logger.debug(String.format("Skipping outdated Telegram message (delta < %ss)", deltaSeconds));
                continue;
            }

            // Если пользователь отсутствует в памяти
            if (!interaction.existsUserById(content.chat().id(), content.userId())) {
                logger.debug("Creating user in memory: chatId=" + content.chat().id() + ", userId=" + content.userId());
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

                logger.debug("Command input detected: " + commandName);

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
                        logger.debug(String.format("Executing command: %s, userId=%d", commandName, interaction.getUserId()));
                        baseCommandClasses.get(commandName).run(interaction);

                    } catch (Exception err) {
                        logger.error(String.format("Invoke method (run) in command \"%s\": %s", commandName, err));
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    logger.debug("Command not found: " + commandName);
                    output.output(interaction.setLanguageValue("system.error.commandNotFound", List.of(commandName)).setInline(false));
                    return;
                }

                // Если что-то ожидаем от пользователя
            } else {
                User user = interaction.getUser(interaction.getUserId());

                if (commandName.startsWith("cancel")) {
                    logger.debug("Cancel command detected");
                    String commandException = user.getCommandException();
                    user.clearExpected(commandException);
                    output.output(interaction.setMessage(String.format("Command \"%s\" is cancel", commandException))
                            .setInline(false));
                    return;
                }

                // Проверка, ожидаем ли мы что-то от пользователя
                if (user.getInputStatus() == User.InputStatus.WAITING) {
                    logger.debug(String.format("Handling expected input: chatId=%d, userId=%d, message=%s",
                            interaction.getChatId(), interaction.getUserId(), message));

                    if (message.equals("/skip")) {
                        user.setValue(message);
                    } else {
                        InputExpectation.UserInputType inputType = user.getInputType();
                        logger.debug("Expected input type: " + inputType);

                        switch (inputType) {

                            case DATE: { // Проверка на дату
                                Optional<LocalDateTime> validDate = validate.isValidDate(message);
                                Optional<LocalDateTime> validTime = validate.isValidDate(message);

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

                            case USER: { // Сохраняем объект пользователя
                                user.setValue(content.tgUser());
                                break;
                            }

                            case CHATMEMBER: { // Сохраняем объект участника
                                user.setValue(content.tgChatMember());
                                break;
                            }

                            case MESSAGE: { // Сохраняем объект сообщения
                                user.setValue(content.tgMessage());
                                break;
                            }

                            case REPLY: { // Сохраняем объект ответного сообщения
                                user.setValue(content.tgMessage().replyToMessage());
                            }

                            default: { // Строка или любой другой тип
                                user.setValue(message);
                                break;
                            }
                        }
                    }

                    logger.debug("Re-invoking command: " + user.getCommandException());
                    baseCommandClasses.get(interaction.getUser(interaction.getUserId())
                            .getCommandException()).run(interaction);
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
                logger.debug("Platform provided from args: " + userPlatform);
            } else {
                output.output(interaction.setMessage("Choose platform (Console, Telegram or All): ").setInline(true));
                // Получаем платформу от пользователя, с консоли
                userPlatform = inputConsole.getString().toLowerCase();
                logger.debug("Platform entered by user: " + userPlatform);
            }


            try {
                // Пытаемся получить платформу
                logger.debug("Platform parsed successfully: " + userPlatform.toUpperCase());
                return LaunchPlatform.valueOf(userPlatform.toUpperCase());

                // Ошибка, если указан неправильная платформа
            } catch (IllegalArgumentException err) {
                logger.debug("Invalid platform input: " + userPlatform);
                output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
            }

        } while (true);
    }
}
