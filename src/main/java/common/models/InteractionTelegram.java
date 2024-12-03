package common.models;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

public class InteractionTelegram extends AbstractInteraction {

    public TelegramBot telegramBot;

    public long timestampBotStart;

    // Объект сообщения (Для Telegram)
    SendMessage sendMessage;


    public InteractionTelegram(TelegramBot telegramBot, long timestampBotStart) {
        this.telegramBot = telegramBot;
        this.timestampBotStart = timestampBotStart;
        this.platform = Platform.TELEGRAM;
    }

    public Interaction setChatId(long chatId) {
        this.chatId = chatId;
        createSendMessage();
        return this;
    }

    public Interaction setMessage(String message) {
        this.message = message;
        createSendMessage();
        return this;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public Message getContentReply() {
        try {
            return content.reply();

        } catch (Exception err) {
            return null;
        }
    }

    @Override
    public String toString() {

        return String.format("InteractionTelegram({"
            + "Token=%s; TIMESTAMP_BOT_START=%s; Platform=%s;"
                + "userId=%s; chatId=%s; Message=%s; Inline=%s; arguments=%s})",
                telegramBot, timestampBotStart, platform, userId, chatId, message, inline, arguments);
    }

    private void createSendMessage() {
        if (this.chatId == 0 || this.message == null) {
            return;
        }

        this.sendMessage = new SendMessage(chatId, message);
    }
}
