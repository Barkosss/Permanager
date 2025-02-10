package common.models;

import common.commands.BaseCommand;
import common.repositories.CommandRepository;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.repositories.WarningRepository;

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

        public static Language getLanguage(String userLang) {
            for (Language lang : Language.values()) {
                if (lang.lang.equalsIgnoreCase(userLang)) {
                    return lang;
                }
            }
            return null;
        }
    }

    Interaction setCommandRepository(CommandRepository commandRepository);

    boolean hasCommand(String command);

    BaseCommand getCommand(String command);

    Interaction setUserRepository(UserRepository userRepository);

    UserRepository getUserRepository();

    User createUser(long chatId, long userId);

    User findUserById(long userId);

    boolean existsUserById(long chatId, long userId);

    Interaction setServerRepository(ServerRepository serverRepository);

    ServerRepository getServerRepository();

    Server createServer(Server server);

    Server findServerById(long chatId);

    Interaction setReminderRepository(ReminderRepository reminderRepository);

    ReminderRepository getReminderRepository();

    Reminder createReminder(Reminder reminder);

    Interaction setWarningRepository(WarningRepository warningRepository);

    WarningRepository getWarningRepository();

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

    Interaction setLanguageValue(String languageKey, List<String> replaces);

    String getLanguageValue(String languageKey);

    String getLanguageValue(String languageKey, List<String> replaces);
}
