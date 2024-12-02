package common.models;

import common.exceptions.MemberNotFoundException;
import common.exceptions.WrongArgumentsException;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.time.LocalDate;
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

    // ...
    UserRepository userRepository;

    // ...
    ServerRepository serverRepository;

    // ...
    ReminderRepository reminderRepository;

    // ...
    Content content;

    // Языковой код
    Language languageCode;

    public Interaction setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public Interaction setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
        return this;
    }

    @Override
    public Interaction setReminderRepository(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
        return this;
    }

    @Override
    public ReminderRepository getReminderRepository() {
        return reminderRepository;
    }

    public User getUser(long userId) {
        try {
            return userRepository.findById(userId);
        } catch (MemberNotFoundException err) {
            return null;
        }
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

    public String getLanguageValue(String languageKey) {
        JSONHandler jsonHandler = new JSONHandler();
        if (jsonHandler.check("content_" + languageCode.getLang() + ".json", languageKey)) {
            return (String) jsonHandler.read("content_" + languageCode.getLang() + ".json", languageKey);
        }
        return "Undefined";
    }

    public String getLanguageValue(String languageKey, List<String> replaces) throws WrongArgumentsException {
        Validate validate = new Validate();
        LoggerHandler logger = new LoggerHandler();
        JSONHandler jsonHandler = new JSONHandler();

        if (jsonHandler.check("content_" + languageCode.getLang() + ".json", languageKey)) {
            String message = (String) jsonHandler.read("content_" + languageCode.getLang() + ".json", languageKey);
            List<String> findReplace = parseReplace(message);

            if (replaces.size() != findReplace.size()) {
                throw new WrongArgumentsException();
            }

            int indexReplace = 0;
            for (String word : findReplace) {
                // Если число
                if (word.charAt(1) == 'i') {
                    Optional<Integer> isInteger = validate.isValidInteger(replaces.get(indexReplace));

                    if (isInteger.isPresent()) {
                        message = message.replaceFirst(word, replaces.get(indexReplace));
                        indexReplace++;
                    } else {
                        // ОШИБКА
                        logger.error("Replace message expected number");
                        throw new WrongArgumentsException();
                    }
                }

                // Если дата
                else if (word.charAt(1) == 'd') {
                    Optional<LocalDate> isLocalDate = validate.isValidDate(replaces.get(indexReplace));

                    if (isLocalDate.isPresent()) {
                        message = message.replaceFirst(word, replaces.get(indexReplace));
                        indexReplace++;
                    } else {
                        // ОШИБКА
                        logger.error("Replace message expected LocalDate");
                        throw new WrongArgumentsException();
                    }
                }

                // Другие типы
                else {
                    message = message.replaceFirst(word, replaces.get(indexReplace));
                    indexReplace++;
                }
            }

            return message;
        }
        return "Undefined";
    }

    private List<String> parseReplace(String message) {
        List<String> array = new ArrayList<>();

        for (String word : message.split(" ")) {
            // Проверяем, что слово начинается и заканчивается на % (Спец символ)
            Pattern pattern = Pattern.compile("%[a-z]?[A-Z]+%");
            Matcher matcher = pattern.matcher(word);
            if (matcher.find()) {
                array.add(matcher.group());
            }
        }

        return array;
    }
}