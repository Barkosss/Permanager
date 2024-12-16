package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.TimeZone;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class SettingsCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    ValidateService validate = new ValidateService();
    LoggerHandler logger = new LoggerHandler();


    @Override
    public String getCommandName() {
        return "settings";
    }

    @Override
    public String getCommandDescription() {
        return "Пользовательские настройки";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String section = arguments.getFirst();
        arguments = arguments.subList(1, arguments.size());
        user.setExcepted(getCommandName(), "section").setValue(section);
        switch (section) {
            case "language": {
                if (arguments.isEmpty()) {
                    return;
                }

                String language = arguments.getFirst().trim().toLowerCase();
                // Сохраняем язык
                switch (language) {
                    case "ru":
                    case "en": {
                        user.setExcepted(getCommandName(), "language").setValue(language);
                        break;
                    }
                }
                break;
            }

            case "timezone": {
                if (arguments.isEmpty()) {
                    return;
                }

                String userTimezone = arguments.get(1).trim().toLowerCase();
                try {
                    Optional<TimeZone> validTimeZone = validate.isValidTimeZone(userTimezone);
                    validTimeZone.ifPresent(timeZone -> user.setExcepted(getCommandName(), "timezone")
                            .setValue(timeZone));
                } catch (Exception err) {
                    System.out.println("Error ID: " + err);
                }
                break;
            }

            default: {
                user.clearExpected(getCommandName(), "section");
                break;
            }
        }
    }

    @Override
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        if (!user.isExceptedKey(getCommandName(), "section")) {
            logger.info("Settings command requested a section argument");
            user.setExcepted(getCommandName(), "section");
            output.output(interaction.setLanguageValue("settings.request"));
            return;
        }


        String section = (String) user.getValue(getCommandName(), "section");
        switch (section) {
            case "language": {
                configLanguage(interaction, user);
                break;
            }

            case "timezone": {
                configTimezone(interaction, user);
                break;
            }

            default: {
                logger.info("Settings command again requested a section argument");
                output.output(interaction.setLanguageValue("settings.error.sectionNotFound"));
                user.clearExpected(getCommandName(), "section");
                break;
            }
        }
    }

    // Настройка языка у пользователя
    private void configLanguage(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "language")) {
            logger.info("Settings command requested a language argument (configLanguage)");
            user.setExcepted(getCommandName(), "language");
            output.output(interaction.setLanguageValue("settings.language.request"));
            return;
        }

        String language = ((String) user.getValue(getCommandName(), "language")).trim().toLowerCase();
        try {
            user.setLanguage(Interaction.Language.getLanguage(language));
            logger.info(String.format("User by id(%s) change the language (%s)", user.getUserId(), user.getLanguage()));
            output.output(interaction.setLanguageValue("settings.language.complete"));
            user.clearExpected(getCommandName());
        } catch (Exception err) {
            logger.info("Unknown error: " + err);
            user.setExcepted(getCommandName(), "language");
            output.output(interaction.setLanguageValue("settings.language.request"));
        }
    }

    // Настройка часового пояса у пользователя
    private void configTimezone(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "timezone")) {
            logger.info("Settings command requested a timezone argument (configTimezone)");
            user.setExcepted(getCommandName(), "timezone");
            output.output(interaction.setLanguageValue("settings.timezone.request"));
            return;
        }

        String timezone = (String) user.getValue(getCommandName(), "timezone");
        try {
            Optional<TimeZone> validTimeZone = validate.isValidTimeZone(timezone);
            if (validTimeZone.isPresent()) {
                logger.info(String.format("User by id(%s) change the timezone (%s)", user.getUserId(), validTimeZone.get()));
                user.setTimeZone(validTimeZone.get());
                output.output(interaction.setLanguageValue("settings.timezone.complete"));
            } else {
                logger.info(String.format("User by id(%s) not change (error) the language (%s)", user.getUserId(), timezone));
                output.output(interaction.setLanguageValue("settings.timezone.error.invalidTimezone"));
            }
        } catch (Exception err) {
            logger.error("Something error: " + err);
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }
}
