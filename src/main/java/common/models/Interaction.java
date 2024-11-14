package common.models;

import common.repositories.UserRepository;

import java.util.List;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM
    }

    Interaction setUserRepository(UserRepository userRepository);

    User getUser(long userId);

    long getUserID();

    Platform getPlatform();

    Interaction setMessage(String message);

    String getMessage();

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();
}
