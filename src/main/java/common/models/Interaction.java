package common.models;

import java.util.List;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM,
        ALL
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
