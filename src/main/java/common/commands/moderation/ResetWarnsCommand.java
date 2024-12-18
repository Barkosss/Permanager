package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.request.GetChatMember;
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
            long userId = (long) user.getValue(getCommandName(), "userId");
            String username = interactionTelegram.telegramBot
                            .execute(new GetChatMember(interaction.getChatId(), userId)).chatMember().user().username();
            output.output(interaction.setLanguageValue("resetWarns.confirmUser",
                    List.of(username)));
            return;
        } else if (!user.isExceptedKey(getCommandName(), "accepted")
                && !user.isExceptedKey(getCommandName(), "userId")) {
            logger.info("...");
            user.setExcepted(getCommandName(), "accepted");
            output.output(interaction.setLanguageValue("resetWarns.confirmAll"));
            return;
        }

        switch (((String) user.getValue(getCommandName(), "accepted")).trim().toLowerCase()) {

            case "y":
            case "yes": {
                logger.info("...");
                StringBuilder message = new StringBuilder();

                // Если надо сбросить предупреждения у конкретного пользователя
                if (user.isExceptedKey(getCommandName(), "userId")) {
                    long userId = (long) user.getValue(getCommandName(), "userId");
                    ChatMember chatMember = interactionTelegram.telegramBot
                            .execute(new GetChatMember(interaction.getChatId(), userId)).chatMember();
                    try {
                        message.append(interaction.getLanguageValue("resetWarns.resetUser",
                                List.of(chatMember.user().username())));
                        interactionTelegram.resetWarnings(interaction.getChatId(), userId);
                    } catch (Exception err) {
                        // ...
                    }
                } else { // Если надо сбросить предупреждения у всех участников
                    try {
                        message.append(interaction.getLanguageValue("resetWarns.resetAll"));
                        interactionTelegram.resetWarnings(interaction.getChatId());
                    } catch (Exception err) {
                        // ...
                    }
                }

                output.output(interaction.setMessage(String.valueOf(message)));
                break;
            }

            case "n":
            case "no": {
                logger.info("...");
                output.output(interaction.setLanguageValue("resetWarns.cancel"));
                break;
            }
        }
        user.clearExpected(getCommandName());
    }
}
