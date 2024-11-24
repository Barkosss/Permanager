package common.models;

import common.repositories.ServerRepository;
import common.repositories.UserRepository;

import java.util.List;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM
    }

    enum Language {
        RUSSIAN,
        ENGLISH
    }

    Interaction setUserRepository(UserRepository userRepository);

    Interaction setServerRepository(ServerRepository serverRepository);

    User getUser(long userId);

    long getUserId();

    Platform getPlatform();

    Interaction setMessage(String message);

    String getMessage();

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();

    Language getLanguageCode();

    Interaction setLanguageCode(Language languageCode);
}
