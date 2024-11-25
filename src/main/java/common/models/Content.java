package common.models;

import java.util.List;

public record Content(long userId, Long chatId, String message, long createdAt,
                      Interaction.Language language, List<String> arguments, Interaction.Platform platform) {

    public String toString() {

        return "Content({"
                + "userId=" + userId
                + "; chatId=" + chatId
                + "; Message=" + message
                + "; CreatedAt=" + createdAt
                + "; Language=" + language
                + "; Arguments=" + arguments
                + "; Platform=" + platform
                + "})";
    }
}