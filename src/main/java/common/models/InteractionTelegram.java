package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import common.exceptions.MemberNotFoundException;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.utils.JSONHandler;

import java.util.List;

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
}
