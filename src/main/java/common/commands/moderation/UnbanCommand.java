package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.enums.ModerationCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class UnbanCommand implements BaseCommand {
    private final LoggerHandler logger = new LoggerHandler();
    private final OutputHandler output = new OutputHandler();
    private final ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "unban";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        // Not user arguments
    }

    @Override
    public void run(Interaction interaction) {

        // Проверка на платформу
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            logger.warning("Attempted to execute \"unban\" from console");
            output.output(interaction.setLanguageValue("system.error."));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        // Проверка на тип беседы
        if (interactionTelegram.execute(new GetChat(interaction.getChatId())).chat().type()
                == ChatFullInfo.Type.Private) {
            logger.warning("Unban attempted in a private chat");
            output.output(interaction.setLanguageValue("system.error."));
            return;
        }

        // Проверка, доступно ли разрешение
        if (!user.hasPermission(interaction.getChatId(), ModerationCommand.UNBAN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied",
                    List.of(interactionTelegram.getUsername())));
            logger.warning("User @" + interactionTelegram.getUsername() + " tried to unban without permission");
            return;
        }

        // Получаем пользователя
        if (!user.isExceptedKey(getCommandName(), "userId")) {
            logger.warning("Expected userId is missing for unban command");
            output.output(interaction.setLanguageValue("..."));
            return;
        }

        try {
            Optional<Long> userIdValid = validate.isValidLong((String) user.getValue(getCommandName(), "userId"));
            if (userIdValid.isEmpty()) {
                logger.warning("Invalid userId provided for unban");
                output.output(interaction.setLanguageValue("..."));
                return;
            }
            long userId = userIdValid.get();

            interactionTelegram.execute(new UnbanChatMember(interaction.getChatId(), userId));
            interactionTelegram.findServerById(interaction.getChatId()).removeUserBan(user);
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been unbaned");
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            output.output(interactionTelegram.setMessage("The user @" + username + " has been unbaned"));


        } catch (Exception err) {
            logger.error("Error: " + err);
            output.output(interaction.setLanguageValue("system.error.something"));
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
