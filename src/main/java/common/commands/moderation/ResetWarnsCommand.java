package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class ResetWarnsCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    ValidateService validate = new ValidateService();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "resetwarns";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        Optional<Integer> validUserId = validate.isValidInteger(arguments.getFirst());
        if (validUserId.isPresent()) {
            user.setExcepted(getCommandName(), "userId").setValue(validUserId);
        }

        if (arguments.isEmpty()) {
            return;
        }

        user.setExcepted(getCommandName(), "reason").setValue(String.join(" ", arguments));
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.RESETWARNS)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        if (!user.isExceptedKey(getCommandName(), "accepted")
                && user.isExceptedKey(getCommandName(), "userId")) {
            logger.info("...");
            user.setExcepted(getCommandName(), "accepted");
            output.output(interaction.setLanguageValue(""));
            return;
        } else if (!user.isExceptedKey(getCommandName(), "accepted")
                && !user.isExceptedKey(getCommandName(), "userId")) {
            logger.info("...");
            user.setExcepted(getCommandName(), "accepted");
            output.output(interaction.setLanguageValue("..."));
            return;
        }

        switch (((String) user.getValue(getCommandName(), "accepted")).trim().toLowerCase()) {

            case "y":
            case "yes": {
                logger.info("");

                // Если надо сбросить предупреждения у конкретного пользователя
                if (user.isExceptedKey(getCommandName(), "userId")) {
                    output.output(interaction.setLanguageValue("..."));
                    interactionTelegram.resetWarnings(interaction.getChatId(),
                            (long) user.getValue(getCommandName(), "userId"));
                } else { // Если надо сбросить предупреждения у всех участников
                    output.output(interaction.setLanguageValue("..."));
                    interactionTelegram.resetWarnings(interaction.getChatId());
                }
            }

            case "n":
            case "no": {
                logger.info("");
                output.output(interaction.setLanguageValue(""));
            }
        }
        user.clearExpected(getCommandName());
    }
}
