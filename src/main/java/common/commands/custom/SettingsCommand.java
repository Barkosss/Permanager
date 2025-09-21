package common.commands.custom;

import common.commands.AbstractCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.TimeZone;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class SettingsCommand extends AbstractCommand {
    OutputHandler output = new OutputHandler();
    ValidateService validate = new ValidateService();
    LoggerHandler logger = new LoggerHandler();


    @Override
    public String getCommandName() {
        return "settings";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        logger.debug(String.format("Parsing arguments for settings: %s", arguments));

        if (arguments.isEmpty()) {
            logger.debug("No arguments provided to settings command.");
            return;
        }

        String section = arguments.getFirst();
        arguments = arguments.subList(1, arguments.size());
        user.setExcepted(getCommandName(), "section").setValue(section);
        switch (section) {
            case "language": {
                if (arguments.isEmpty()) {
                    logger.debug("Language section selected, but no language argument provided.");
                    return;
                }

                String language = arguments.getFirst().trim().toLowerCase();
                logger.debug(String.format("Attempting to set language to: %s", language));
                // Сохраняем язык
                switch (language) {
                    case "ru":
                    case "en": {
                        user.setExcepted(getCommandName(), "language").setValue(language);
                        logger.info(String.format("Language argument parsed successfully: %s", language));
                        break;
                    }
                    default: logger.warning(String.format("Invalid language argument: %s", language));
                }
                break;
            }

            case "timezone": {
                if (arguments.isEmpty()) {
                    logger.debug("Timezone section selected, but no timezone argument provided.");
                    return;
                }

                String userTimezone = arguments.get(1).trim().toLowerCase();
                logger.debug(String.format("Attempting to validate timezone: %s", userTimezone));
                try {
                    Optional<TimeZone> validTimeZone = validate.isValidTimeZone(userTimezone);
                    validTimeZone.ifPresent(timeZone -> {
                        user.setExcepted(getCommandName(), "timezone").setValue(timeZone);
                        logger.info(String.format("Timezone validated and set: %s", userTimezone));
                    });
                    if (validTimeZone.isEmpty()) {
                        logger.warning(String.format("Invalid timezone provided: %s", userTimezone));
                    }
                } catch (Exception err) {
                    logger.error("Error validating timezone: " + err);
                }
                break;
            }

            default: {
                logger.warning(String.format("Unknown section provided: %s", section));
                user.clearExpected(getCommandName(), "section");
                break;
            }
        }
    }

    @Override
    public void run(Interaction interaction) {
        logger.info("Executing settings command");
        User user = interaction.getUser(interaction.getUserId());

        if (!user.isExceptedKey(getCommandName(), "section")) {
            logger.info("Settings command requested a section argument");
            user.setExcepted(getCommandName(), "section");
            Object timezone = user.getTimeZone();
            if (timezone == null) {
                timezone = interaction.getLanguageValue("system.undefined");
            }
            Interaction.Language language = user.getLanguage();
            try {
                output.output(interaction.setLanguageValue("settings.start", List.of(
                        String.valueOf(language),
                        String.valueOf(timezone)
                )));
                output.output(interaction.setLanguageValue("settings.request"));
            } catch (Exception err) {
                logger.error("Exception occurred in settings.run(): " + err);
                output.output(interaction.setLanguageValue("system.error.something"));
                user.clearExpected(getCommandName());
            }
            return;
        }

        String section = (String) user.getValue(getCommandName(), "section");
        logger.debug("Running settings section: " + section);
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
                logger.warning("Unrecognized section in run: " + section);
                output.output(interaction.setLanguageValue("settings.error.sectionNotFound"));
                user.setExcepted(getCommandName(), "section");
                break;
            }
        }
    }

    // Настройка языка у пользователя
    private void configLanguage(Interaction interaction, User user) {
        logger.debug("Configuring language");

        if (!user.isExceptedKey(getCommandName(), "language")) {
            logger.info("Settings command requested a language argument (configLanguage)");
            user.setExcepted(getCommandName(), "language");
            output.output(interaction.setLanguageValue("settings.language.request"));
            return;
        }

        String language = ((String) user.getValue(getCommandName(), "language")).trim().toLowerCase();
        logger.debug("User provided language: " + language);
        try {
            user.setLanguage(Interaction.Language.getLanguage(language));
            logger.info(String.format("User by id(%s) change the language (%s)", user.getUserId(), user.getLanguage()));
            output.output(interaction.setLanguageValue("settings.language.complete"));
            user.clearExpected(getCommandName());
        } catch (Exception err) {
            logger.error("Failed to set language: " + err);
            user.setExcepted(getCommandName(), "language");
            output.output(interaction.setLanguageValue("settings.language.request"));
        }
    }

    // Настройка часового пояса у пользователя
    private void configTimezone(Interaction interaction, User user) {
        logger.debug("Configuring timezone");

        if (!user.isExceptedKey(getCommandName(), "timezone")) {
            logger.info("Settings command requested a timezone argument (configTimezone)");
            user.setExcepted(getCommandName(), "timezone");
            output.output(interaction.setLanguageValue("settings.timezone.request"));
            return;
        }

        String timezone = (String) user.getValue(getCommandName(), "timezone");
        logger.debug("User provided timezone: " + timezone);
        try {
            Optional<TimeZone> validTimeZone = validate.isValidTimeZone(timezone);
            if (validTimeZone.isPresent()) {
                logger.info(String.format("User by id(%s) change the timezone (%s)",
                        user.getUserId(), validTimeZone.get()));
                user.setTimeZone(validTimeZone.get());
                output.output(interaction.setLanguageValue("settings.timezone.complete"));

            } else {
                logger.info(String.format("User by id(%s) not change (error) the language (%s)",
                        user.getUserId(), timezone));
                output.output(interaction.setLanguageValue("settings.timezone.error.invalidTimezone"));
            }
        } catch (Exception err) {
            logger.error("Failed to validate/set timezone: " + err);
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }
}
