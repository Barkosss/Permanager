package common.models;

import common.exceptions.WrongArgumentsException;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;

import java.util.List;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM
    }

    enum Language {
        RUSSIAN("ru"),
        ENGLISH("en");

        private final String lang;

        Language(String lang) {
            this.lang = lang;
        }

        public String getLang() {
            return lang;
        }
    }

    UserRepository getUserRepository();

    Interaction setUserRepository(UserRepository userRepository);

    Interaction setServerRepository(ServerRepository serverRepository);

    Interaction setReminderRepository(ReminderRepository reminderRepository);

    ReminderRepository getReminderRepository();

    User getUser(long userId);

    long getUserId();

    Interaction setUserId(long userId);

    long getChatId();

    Platform getPlatform();

    Interaction setMessage(String message);

    String getMessage();

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();

    Language getLanguageCode();

    Interaction setLanguageCode(Language languageCode);

    Interaction setContent(Content content);

    Interaction setLanguageValue(String languageKey);

    Interaction setLanguageValue(String languageKey, List<String> replaces) throws WrongArgumentsException;

    String getLanguageValue(String languageKey);

    String getLanguageValue(String languageKey, List<String> replaces) throws WrongArgumentsException;
}
