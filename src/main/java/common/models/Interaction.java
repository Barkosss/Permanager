package common.models;

import com.pengrad.telegrambot.TelegramBot;

public class Interaction {

    public static TelegramBot TELEGRAM_BOT;


    long userID;

    String message;

    public Interaction(TelegramBot telegramBot) {
        TELEGRAM_BOT = telegramBot;
    }

    public Interaction(String message) {
        this.message = message;
    }

    public Interaction(long userID, String message) {
        this.message = message;
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
