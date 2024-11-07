package common.models;

import java.util.List;

public interface Interaction {

    enum Platform {
        CONSOLE("console"),
        TELEGRAM("telegram"),
        DISCORD("discord"),
        ALL("all");

        final String type;

        Platform(String type) {
            this.type = type;
        }
    }

    enum UserInputType {
        INT,
        STRING,
        DATE,
    }

    Platform getPlatform();

    Interaction setPlatform(Platform platform);

    Interaction setMessage(String message);

    String getMessage();

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();

    InputExpectation getUserInputExpectation();
}
