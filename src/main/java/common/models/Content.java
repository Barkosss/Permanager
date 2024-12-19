package common.models;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;

import java.util.List;

public record Content(String username, long userId, Chat chat, Message reply, String message, long createdAt,
                      Interaction.Language language, List<String> arguments, Interaction.Platform platform) {


    public String toString() {

        return String.format("Content({username=%s"
                + "; userId=%s; chat=%s; reply=%s; Message=%s; CreatedAt=%s; Language=%s; Arguments=%s; Platform=%s})",
                username, userId, chat, reply, message, createdAt, language, arguments, platform);
    }
}