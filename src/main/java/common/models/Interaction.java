package common.models;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM
    }

    enum Type {
        INT,
        STRING,
        DATE,
    }

    Platform getPlatform();

    Interaction setPlatform(Platform platform);

    Interaction setMessage(String message);

    String getMessage();

    default Interaction setInlineKeyboard(InlineKeyboardMarkup inlineKeyboard) {
        return null;
    }

    default Interaction setReplyKeyboard(ReplyKeyboardMarkup replyKeyboard) {
        return null;
    }

    default Interaction replyKeyboardRemove() {
        return null;
    }

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();

    String getInputCommandName();

    Interaction setInputCommandName(String inputCommandName);

    String getInputKey();

    Interaction setInputKey(String inputKey);

    void getValue(String commandName, String key);

    void getValueInt(String commandName, String key);

    Interaction getValueDate(String commandName, String key);

    Map<String, Map<String, String>> getExpectedInput();

    Interaction setValue(Map<String, Map<String, String>> expectedInput);

    void clearExpectedInput(String commandName);

    default void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException err) {
            System.out.println("[ERROR] Timeout thread: " + err);
        }
    }
}
