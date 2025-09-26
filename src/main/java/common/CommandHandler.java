package common;

import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;
import common.commands.AbstractCommand;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
            Set<Class<? extends AbstractCommand>> subclasses = reflections.getSubTypesOf(AbstractCommand.class);
            logger.info("Found " + subclasses.size() + " command classes");

            String commandName;
            BaseCommand instanceClass;
            // Проходимся по каждому классу
            for (Class<? extends BaseCommand> subclass : subclasses) {
                // Создаём экземпляр класса
                instanceClass = subclass.getConstructor().newInstance();
                commandName = instanceClass.getCommandName().toLowerCase();
                logger.trace("Trying to load command: " + commandName);

                // Если название команды пустое, то пропускаем ход
                if (commandName.isEmpty()) {
                    logger.warning("Skipped command with empty name: " + subclass.getSimpleName());
                    continue;
                }

                // Проверка, нет ли команд с таким именем в мапе
                if (!baseCommandClasses.containsKey(commandName)) {
                    // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                    baseCommandClasses.put(commandName, instanceClass);
                    logger.info("Command registered: " + commandName);
                } else {
                    String errMessage = String.format("There was a duplication of the command - %s", commandName);
                    logger.fatal(errMessage, true);
                    System.exit(0);
                }
            }

            commandRepository = new CommandRepository(baseCommandClasses);
            serverRepository.setCommandRepository(commandRepository);
            logger.info("CommandRepository initialized successfully (size=" + baseCommandClasses.size() + ")");

        } catch (Exception err) {
            logger.fatal(String.format("Command loader: %s", err));
        }
    }

    // Запуск программы
    public void launch(Interaction interaction, LaunchPlatform platform) {
        logger.info("Launching application with platform: " + platform);

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
            logger.info("Creating default console user");
            userRepository.create(0, 0L);
            // Поток для Console
            logger.debug("Starting Console thread");
            Thread threadConsole = getThreadConsole();
            threadConsole.start();
            logger.info("SYSTEM: Console is launch", true);
        }

        logger.info("System fully initialized.");
        try (ScheduledExecutorService schedulerDeleteMessage = Executors.newSingleThreadScheduledExecutor()) {
            schedulerDeleteMessage.schedule(() -> System.out.println("Program is launch"), 5, TimeUnit.SECONDS);
        } catch (Exception err) {
            logger.error("Failed to schedule message program is launch: " + err.getMessage());
        }
    }

    @NotNull
    private Thread getThreadTelegram(Interaction interaction) {
        logger.trace("Creating Telegram thread object");
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
        logger.trace("Creating Console thread object");
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
        logger.info(String.format("Processing %s updates", contents.size()));

        for (Content content : contents) {
            interaction.setContent(content);
            logger.debug("Processing content: " + content);

            // Если сообщение в Telegram было отправлено во время offline
            long deltaSeconds = 30;
            if (content.platform() == Interaction.Platform.TELEGRAM
                    && (content.createdAt() <= ((InteractionTelegram) interaction).getTimestampBotStart() - deltaSeconds)) {
                logger.warning(String.format("Skipping outdated Telegram message (delta < %ss)", deltaSeconds));
                continue;
            }

            // Если пользователь отсутствует в памяти (Telegram)
            if (content.platform() == Interaction.Platform.TELEGRAM && !interaction.existsUserById(content.chat().id(), content.userId())) {
                logger.info("Creating user in memory: chatId=" + content.chat().id() + ", userId=" + content.userId());
                interaction.createUser(content.chat().id(), content.userId());
            }

            String message = content.message();
            List<String> args = List.of(message.split(" "));
            String commandName = args.getFirst().toLowerCase().substring(1);
            String commandBotName = null;

            User user = interaction.getUser(content.userId());

            // Берём название команды до "@"
            if (message.startsWith("/") && message.charAt(1) != ' ' && commandName.contains("@")) {
                List<String> commandParse = List.of(commandName.split("@"));
                commandName = commandParse.getFirst();
                commandBotName = commandParse.getLast();
            }

            GetMeResponse bot = ((InteractionTelegram) interaction).execute(new GetMe());
            if (commandBotName != null && !bot.user().username().equalsIgnoreCase(commandBotName)) {
                return;
            }

            // Проверка, что это команда
            if (message.startsWith("/") && message.charAt(1) != ' '
                    && user.getInputStatus() == User.InputStatus.COMPLETED) {

                logger.debug("Command input detected: " + commandName);

                if (commandName.startsWith("exit")
                        && (interaction.getPlatform() == Interaction.Platform.CONSOLE
                        || List.of(746875461L, 0L).contains(interaction.getUserId()))) {
                    logger.info("Program is stop", true);
                    ((InteractionTelegram) interaction).getTelegramBot().shutdown();
                    System.exit(0);
                }

                interaction.setMessage(message).setArguments(args.subList(1, args.size()))
                        .setLanguageCode(content.language());

                // Если введённая команда имеется в хэшмап
                if (baseCommandClasses.containsKey(commandName)) {

                    // Запустить класс, в котором будет работать команда
                    try {
                        logger.debug(String.format("Executing command: %s, userId=%d", commandName, interaction.getUserId()));
                        baseCommandClasses.get(commandName).commandHandler(interaction);

                    } catch (Exception err) {
                        logger.error(String.format("Invoke method (commandHandler) in command \"%s\": %s", commandName, err));
                    }

                } else {
                    // Ошибка: Команда не найдена.
                    logger.warning("Command not found: " + commandName);
                    output.output(interaction.setLanguageValue("system.error.commandNotFound", List.of(commandName)).setInline(false));
                    return;
                }

                // Если что-то ожидаем от пользователя
            } else if (user.getInputStatus() == User.InputStatus.WAITING) {
                handleExpectedInput(interaction, content, user, message);
            }
        }
    }

    private void handleExpectedInput(Interaction interaction, Content content, User user, String message) {
        logger.debug("Handling expected input for user: " + user.getUserId());

        if (message.startsWith("cancel")) {
            String commandException = user.getCommandException();
            logger.info("Cancelling command: " + commandException);
            user.clearExpected(commandException);
            output.output(interaction.setMessage(String.format("Command \"%s\" is cancel", commandException))
                    .setInline(false));
            return;
        }

        logger.debug(String.format("Handling expected input: chatId=%d, userId=%d, message=%s",
                interaction.getChatId(), interaction.getUserId(), message));

        if (message.equals("/skip")) {
            user.setValue(message);
        } else {
            InputExpectation.UserInputType inputType = user.getInputType();
            logger.trace("Expected input type: " + inputType);

            switch (inputType) {

                // Проверка на дату
                case DATE -> {
                    Optional<LocalDateTime> validDate = validate.isValidDate(message);
                    Optional<LocalDateTime> validTime = validate.isValidDate(message);

                    validDate.ifPresentOrElse(user::setValue, () -> validTime.ifPresent(user::setValue));
                }

                // Проверка на число (Integer)
                case INTEGER -> validate.isValidInteger(message).ifPresent(user::setValue);

                // Проверка на неотрицательное число (Integer)
                case UNSIGNED_INTEGER -> validate.isValidInteger(message)
                        .filter(validInteger -> validInteger >= 0)
                        .ifPresent(user::setValue);

                // Проверка на число (Long)
                case LONG -> validate.isValidLong(message).ifPresent(user::setValue);

                // Проверка на неотрицательное число (Integer)
                case UNSIGNED_LONG -> validate.isValidLong(message)
                        .filter(validLong -> validLong >= 0)
                        .ifPresent(user::setValue);

                // Сохраняем объект пользователя
                case USER -> user.setValue(content.tgUser());

                // Сохраняем объект участника
                case CHATMEMBER -> user.setValue(content.tgChatMember());

                // Сохраняем объект сообщения
                case MESSAGE -> user.setValue(content.tgMessage());

                // Сохраняем объект ответного сообщения
                case REPLY -> user.setValue(content.tgMessage().replyToMessage());

                // Строка или любой другой тип
                default -> user.setValue(message);
            }
        }

        logger.debug("Re-invoking command: " + user.getCommandException());
        baseCommandClasses.get(user.getCommandException()).commandHandler(interaction);
    }

    // Настройка взаимодействий и запуск программы
    public LaunchPlatform choosePlatform(String[] args) {
        Interaction interaction = new InteractionConsole();
        String userPlatform;

        do {
            if (args.length > 0 && List.of("console", "telegram", "all").contains(args[0].toLowerCase())) {
                userPlatform = args[0];
                logger.debug("Platform from args: " + userPlatform);
            } else {
                output.output(interaction.setMessage("Choose platform (Console, Telegram or All): ").setInline(true));
                // Получаем платформу от пользователя, с консоли
                userPlatform = inputConsole.getString().toLowerCase();
                logger.trace("User input platform: " + userPlatform);
            }


            try {
                // Пытаемся получить платформу
                LaunchPlatform platform = LaunchPlatform.valueOf(userPlatform.toUpperCase());
                logger.info("Platform selected: " + platform);
                return platform;

                // Ошибка, если указан неправильная платформа
            } catch (IllegalArgumentException err) {
                logger.warning("Invalid platform input: " + userPlatform);
                output.output(interaction.setMessage("No, there is no such platform. Try again.").setInline(false));
            }

        } while (true);
    }
}
