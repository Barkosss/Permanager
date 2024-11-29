package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
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

public class InteractionTelegram implements Interaction {

    public TelegramBot telegramBot;

    public long timestampBotStart;

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Идентификатор чата
    long chatId;

    // Идентификатор пользователя
    long userId;

    // Полное сообщение
    String message;

    // Объект сообщения (Для Telegram)
    SendMessage sendMessage;

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

    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        this.telegramBot = telegramBot;
        this.timestampBotStart = timestampBotStart;
        this.platform = Platform.TELEGRAM;
    }

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

    public Interaction setChatId(long chatId) {
        this.chatId = chatId;
        createSendMessage();
        return this;
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

    public Interaction setMessage(String message) {
        this.message = message;
        createSendMessage();
        return this;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
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

    public Message getContentReply() {
        try {
            return content.reply();

        } catch (Exception err) {
            return null;
        }
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

            int indexReplace = 0;
            for (String word : findReplace) {
                // Если число
                if (word.charAt(1) == 'i') {
                    Optional<Integer> isInteger = validate.isValidInteger(replaces.get(indexReplace));

                    if (isInteger.isPresent()) {
                        message = message.replace(word, replaces.get(indexReplace));
                        indexReplace++;
                    } else {
                        // ОШИБКА
                        logger.error("");
                        throw new WrongArgumentsException();
                    }
                }

                // Если дата
                else if (word.charAt(1) == 'd') {
                    Optional<LocalDate> isLocalDate = validate.isValidDate(replaces.get(indexReplace));

                    if (isLocalDate.isPresent()) {
                        message = message.replace(word, replaces.get(indexReplace));
                        indexReplace++;
                    } else {
                        // ОШИБКА
                        logger.error("");
                        throw new WrongArgumentsException();
                    }
                }

                // Другие типы
                else {
                    message = message.replace(word, replaces.get(indexReplace));
                    indexReplace++;
                }
            }

            return message;
        }
        return "Undefined";
    }

    @Override
    public String toString() {

        return "InteractionConsole({"
                + "Token=" + telegramBot
                + "; TIMESTAMP_BOT_START=" + timestampBotStart
                + "; Platform=" + platform
                + "; userId=" + userId
                + "; chatId=" + chatId
                + "; Message=" + message
                + "; Inline=" + inline
                + "; arguments=" + arguments
                + "})";
    }

    private void createSendMessage() {
        if (this.chatId == 0 || this.message == null) {
            return;
        }

        this.sendMessage = new SendMessage(chatId, message);
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
