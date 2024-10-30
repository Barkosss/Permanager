package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import common.exceptions.MemberNotFoundException;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;

public class InteractionTelegram implements Interaction {

    public final TelegramBot TELEGRAM_BOT;

    public final long TIMESTAMP_BOT_START;

    // Платформа: Terminal, Telegram, Discord
    Platform platform;

    // Для Telegram - Chat ID, для Discord - User ID
    long userID;

    // Полное сообщение
    String message;

    // Объект сообщения (Для Telegram)
    SendMessage sendMessage;

    // Выводить в одну строку или нет
    boolean inline;

    // Массив аргументов в сообщении (разделитель - пробел)
    List<String> arguments;

    // Какой тип ожидается от пользователя
    Type userInputType;

    // Название команды
    String inputCommandName;

    // Какое значение требуется (ключ Map)
    String inputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInput;

    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        TELEGRAM_BOT = telegramBot;
        TIMESTAMP_BOT_START = timestampBotStart;
    }

    public InteractionTelegram setUpdate(Update update) {
        this.userID = update.chatId;
        this.message = update.message;
        this.arguments = update.arguments;
        return this;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Interaction setPlatform(Platform platform) {
        this.platform = platform;
        return this;
    }

    public long getUserID() {
        return userID;
    }

    public InteractionTelegram setUserID(long userID) {
        this.userID = userID;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Interaction setMessage(String message) {
        this.message = message;
        return this;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public InteractionTelegram setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
        return this;
    }

    public InteractionTelegram setInlineKeyboard(InlineKeyboardMarkup inlineKeyboard) {

        if (sendMessage == null) {
            try {
                sendMessage = new SendMessage(userID, message).replyMarkup(inlineKeyboard);
            } catch(Exception err) { // Если не получилось создать объект SendMessage -> Нет userID или некорректный inlineKeyboard
                System.out.println("[ERROR] Interaction Telegram set inline keyboard: " + err);
            }
        } else {
            sendMessage.replyMarkup(inlineKeyboard);
        }

        return this;
    }

    public InteractionTelegram setReplyKeyboard(ReplyKeyboardMarkup replyKeyboard) {

        if (sendMessage == null) {
            try {
                sendMessage = new SendMessage(userID, message).replyMarkup(replyKeyboard);
            } catch(Exception err) { // Если не получилось создать объект SendMessage -> Нет userID или некорректный inlineKeyboard
                System.out.println("[ERROR] Interaction Telegram set reply keyboard: " + err);
            }
        } else {
            sendMessage.replyMarkup(replyKeyboard);
        }

        return this;
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

    public String getInputCommandName() {
        return inputCommandName;
    }

    public Interaction setInputCommandName(String inputCommandName) {
        this.inputCommandName = inputCommandName;
        return this;
    }

    public String getInputKey() {
        return inputKey;
    }

    public Interaction setInputKey(String inputKey) {
        this.inputKey = inputKey;
        return this;
    }

    public void getValue(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.STRING;
    }

    public void getValueInt(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.INT;
    }

    public InteractionTelegram getValueDate(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Type.DATE;
        return this;
    }

    public Map<String, Map<String, String>> getExpectedInput() {
        return expectedInput;
    }

    public Interaction setValue(Map<String, Map<String, String>> expectedInput) {
        this.expectedInput = expectedInput;
        return this;
    }

    public void clearExpectedInput(String commandName) {
        inputKey = inputCommandName = null;
        userInputType = null;
        expectedInput.get(commandName).clear();
    }

    // Метод для поиска числа в аргументах
    public Optional<Integer> getInt() {
        Validate validate = new Validate();
        Optional<Integer> value;
        for(String argument : arguments) {
            value = validate.isValidInteger(argument);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    // Метод для поиска даты в аргументах
    public Optional<LocalDate> getDate() {
        Validate validate = new Validate();
        Optional<LocalDate> value;
        for(String argument : arguments) {
            value = validate.isValidDate(argument);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }
}