package common.models;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;

import java.util.List;

public record Content(long userId, Chat chat, Message reply, String message, long createdAt,
                      Interaction.Language language, List<String> arguments, Interaction.Platform platform) {


    public String toString() {

        return "Content({"
                + "; userId=" + userId
                + "; chat=" + chat
                + "; reply=" + reply
                + "; Message=" + message
                + "; CreatedAt=" + createdAt
                + "; Language=" + language
                + "; Arguments=" + arguments
                + "; Platform=" + platform
                + "})";
    }
}