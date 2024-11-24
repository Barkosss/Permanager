package common.models;

import java.util.List;

public record Content(Long userId, String message, long createdAt,
                      Interaction.Language language, List<String> arguments, Interaction.Platform platform) {

    public String toString() {

        return "Content({"
                + "userId=" + userId
                + "; Message=" + message
                + "; CreatedAt=" + createdAt
                + "; Language=" + language
                + "; Arguments=" + arguments
                + "; Platform=" + platform
                + "})";
    }
}