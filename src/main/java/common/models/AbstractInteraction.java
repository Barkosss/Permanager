package common.models;

import common.commands.BaseCommand;
import common.enums.ModerationCommand;
import common.repositories.CommandRepository;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.repositories.WarningRepository;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractInteraction implements Interaction {

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Идентификатор чата
    long chatId;

    // Идентификатор пользователя
    long userId;

    // Полное сообщение
    String message;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // Хранилище команд бота
    CommandRepository commandRepository;

    // Хранилище пользователей
    UserRepository userRepository;

    // Хранилище серверов/чатов
    ServerRepository serverRepository;

    // Хранилище напоминание
    ReminderRepository reminderRepository;

    // Хранилище предупреждений
    WarningRepository warningRepository;

    // Содержимое от Telegram Updates
    Content content;

    // Языковой код
    Language languageCode;

    public Interaction setCommandRepository(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
        return this;
    }

    public boolean hasCommand(String command) {
        return this.commandRepository.hasCommand(command);
    }

    public Optional<ModerationCommand> getCommand(String command) {
        return ModerationCommand.ALL.getCommand(command);
    }

    public Interaction setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public User createUser(long chatId, long userId) {
        if (this.userRepository == null) {
            this.userRepository = new UserRepository();
        }

        return this.userRepository.create(chatId, userId);
    }

    public User findUserById(long userId) {
        if (this.userRepository == null) {
            this.userRepository = new UserRepository();
        }

        return this.userRepository.findById(chatId, userId);
    }

    public boolean existsUserById(long chatId, long userId) {
        if (this.userRepository == null) {
            this.userRepository = new UserRepository();
        }

        return this.userRepository.existsById(chatId, userId);
    }

    public Interaction setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
        return this;
    }

    public ServerRepository getServerRepository() {
        return serverRepository;
    }

    public Server createServer(Server server) {
        return this.serverRepository.create(server);
    }

    public boolean existsServerById(long chatId) {
        return this.serverRepository.existsById(chatId);
    }

    public Server findServerById(long chatId) {
        return this.serverRepository.findById(chatId);
    }

    public void removeServer(long chatId) {
        this.serverRepository.remove(chatId);
    }

    public Interaction setReminderRepository(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
        return this;
    }

    public ReminderRepository getReminderRepository() {
        return reminderRepository;
    }

    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.create(reminder);
    }

    public boolean existsReminderByTimestamp(long reminderId) {
        return this.reminderRepository.existsByTimestamp(reminderId);
    }

    public List<Reminder> findReminderByTimestamp(long timestamp) {
        return this.reminderRepository.findByTimestamp(timestamp);
    }

    public void removeReminder(long timestamp) {
        this.reminderRepository.remove(timestamp);
    }

    public Interaction setWarningRepository(WarningRepository warningRepository) {
        this.warningRepository = warningRepository;
        return this;
    }

    public WarningRepository getWarningRepository() {
        return warningRepository;
    }

    public Warning createWarning(Warning warning) {
        return this.warningRepository.create(warning);
    }

    public boolean existsWarningById(long chatId, long userId, long warningId) {
        return this.warningRepository.existsById(chatId, userId, warningId);
    }

    public Warning findWarningById(long chatId, long userId, long warningId) {
        return this.warningRepository.findById(chatId, userId, warningId);
    }

    public void removeWarning(long chatId, long userId, long warningId) {
        this.warningRepository.remove(findWarningById(chatId, userId, warningId));
    }

    public User getUser(long userId) {
        if (this.userRepository == null) {
            this.userRepository = new UserRepository();
            this.userRepository.create(chatId, userId);
        }

        return userRepository.findById(chatId, userId);
    }

    public Platform getPlatform() {
        return platform;
    }

    public long getChatId() {
        return chatId;
    }

    public long getUserId() {
        return userId;
    }

    public Interaction setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public boolean getInline() {
        return inline;
    }

    public Interaction setInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public Interaction setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Interaction setContent(Content content) {
        this.content = content;
        return this;
    }

    public Language getLanguageCode() {
        return languageCode;
    }

    public Interaction setLanguageCode(Language languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public Interaction setLanguageValue(String languageKey) {
        this.message = getLanguageValue(languageKey);
        return this;
    }

    public Interaction setLanguageValue(String languageKey, List<String> replaces) {
        try {
            this.message = getLanguageValue(languageKey, replaces);
        } catch (Exception err) {
            LoggerHandler logger = new LoggerHandler();
            logger.error("Incorrect number of arguments to replace (setLanguageValue: String, List<>)");
        }
        return this;
    }

    public String getLanguageValue(String languageKey) {
        JSONHandler jsonHandler = new JSONHandler();
        String commandName = "";
        Optional<String> optionalCommandName = getCommandNameFromStack(4).or(() -> getCommandNameFromStack(2));

        if (optionalCommandName.isPresent()) {
            commandName = optionalCommandName.get();
        }

        if (languageKey.startsWith(".")) {
            languageKey = commandName + languageKey;
        }

        String languagePath = String.format("content_%s.json", getUser(userId).getLanguage().getLang());
        if (jsonHandler.check(languagePath, languageKey)) {
            return (String) jsonHandler.read(languagePath, languageKey);
        }
        return languageKey;
    }

    public String getLanguageValue(String languageKey, List<String> replaces) {
        ValidateService validate = new ValidateService();
        LoggerHandler logger = new LoggerHandler();

        String message = getLanguageValue(languageKey);
        if (message == null) {
            return languageKey;
        }
        List<String> findReplace = parseReplace(message);

        if (replaces.size() < findReplace.size()) {
            findReplace = findReplace.subList(0, replaces.size());
        }

        int indexReplace = 0;
        for (String word : findReplace) {
            if (word.charAt(1) == 'i') { // Если число
                Optional<Integer> isInteger = validate.isValidInteger(replaces.get(indexReplace));

                if (isInteger.isPresent()) {
                    message = message.replaceFirst(word, replaces.get(indexReplace));
                    indexReplace++;
                } else {
                    logger.error("Replace message expected number");
                }
            } else if (word.charAt(1) == 'd') { // Если дата
                Optional<LocalDateTime> isLocalDate = validate.isValidDate(replaces.get(indexReplace));

                if (isLocalDate.isPresent()) {
                    message = message.replaceFirst(word, replaces.get(indexReplace));
                    indexReplace++;
                } else {
                    logger.error("Replace message expected LocalDateTime");
                }
            } else { // Другие типы
                message = message.replaceFirst(word, replaces.get(indexReplace));
                indexReplace++;
            }
        }

        return message;
    }

    private Optional<String> getCommandNameFromStack(int depth) {
        LoggerHandler logger = new LoggerHandler();
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (depth < 0 || depth >= stackTrace.length) {
                logger.debug("In an AbstractInteraction with getCommandNameFromStack, the depth is greater than stackTrace or the depth is less than zero");
                return Optional.empty();
            }
            StackTraceElement stack = stackTrace[depth];
            Class<?> commandClass = Class.forName(stack.getClassName());
            if (BaseCommand.class.isAssignableFrom(commandClass)) {
                BaseCommand method = (BaseCommand) commandClass.getConstructor().newInstance();
                logger.debug("(AbstractInteraction, getCommandNameFromStack) Method is find");
                return Optional.of(method.getCommandName());
            }
            logger.debug("(AbstractInteraction, getCommandNameFromStack) Method isn't find");
            return Optional.empty();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException
                 | InvocationTargetException err) {
            logger.error("Error (AbstractInteraction, getCommandNameFromStack): " + err);
            return Optional.empty();
        }
    }

    private List<String> parseReplace(String message) {
        List<String> array = new ArrayList<>();

        for (String word : message.split(" ")) {
            // Проверяем, что слово начинается и заканчивается на % (Спец символ)
            Pattern pattern = Pattern.compile("%[a-z]?[A-Z_]+%");
            Matcher matcher = pattern.matcher(word);
            if (matcher.find()) {
                array.add(matcher.group());
            }
        }

        return array;
    }
}