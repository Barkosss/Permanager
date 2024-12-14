package common.commands.custom;

import common.commands.BaseCommand;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;
import common.models.TimeZone;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class SettingsCommand implements BaseCommand {
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
        System.out.println("arguments: " + arguments);

        if (arguments.isEmpty()) {
            return;
        }

        String section = arguments.getFirst();
        System.out.println("section: " + section);
        user.setExcepted(getCommandName(), "section").setValue(section);
        switch (section) {
            case "language": {
                if (arguments.isEmpty()) {
                    return;
                }

                String language = arguments.get(1).trim().toLowerCase();
                // Сохраняем язык
                switch (language) {
                    case "ru": {
                    }
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
                // ...
                break;
            }
        }
    }

    private void configLanguage(Interaction interaction, User user) {


    }

    private void configTimezone(Interaction interaction, User user) {


    }
}
