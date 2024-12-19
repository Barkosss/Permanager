package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class RemWarnCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "remwarn";
    }

    @Override
    public String getCommandDescription() {
        return "Снять предупреждение с пользователя";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        if (arguments.isEmpty()) {
            return;
        }

        Optional<Long> validUserId = validate.isValidLong(arguments.getFirst());
        if (validUserId.isPresent()) {
            logger.debug(String.format("User by id(%s) is valid", validUserId.get()));
            ChatMember chatMember = interactionTelegram.telegramBot
                    .execute(new GetChatMember(interaction.getChatId(), validUserId.get())).chatMember();

            if (chatMember != null) {
                logger.debug(String.format("ChatMember is null (userId=%s)", validUserId.get()));
                user.setExcepted(getCommandName(), "user").setValue(chatMember.user());
                arguments = arguments.subList(1, arguments.size());
            }
        } else if (interactionTelegram.getContentReply() != null) {
            logger.debug("Get user from reply message for remove warning");
            Message content = interactionTelegram.getContentReply();
            user.setExcepted(getCommandName(), "user").setValue(content.from());
        }

        if (arguments.isEmpty()) {
            return;
        }

        Optional<Integer> validIndex = validate.isValidInteger(arguments.getFirst());
        if (validIndex.isPresent()) {
            logger.debug(String.format("Index warning (%s) is valid", validIndex.get()));
            user.setExcepted(getCommandName(), "index").setValue(validIndex.get());
        }
    }

    @Override
    public void run(Interaction interaction) {

        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        if (interactionTelegram.telegramBot.execute(new GetChat(interaction.getChatId())).chat().type()
                == ChatFullInfo.Type.Private) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.REMWARN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        parseArgs(interactionTelegram, user);

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("RemWarn command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("remWarn.replyMessage"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            Message content = interactionTelegram.getContentReply();

            // Если пользователь это бот или взаимодействующий
            if (content.from().isBot() || content.from().id() == interaction.getUserId()) {
                logger.debug(String.format("User by id(%s) is bot or caller of the command (%s)",
                        content.from().id(), getCommandName()));
                user.setExcepted(getCommandName(), "user");
                output.output(interaction.setLanguageValue("remWarn.replyMessage"));
                return;
            }

            user.setExcepted(getCommandName(), "user").setValue(content.from());
        }

        if (!user.isExceptedKey(getCommandName(), "index")) {
            logger.info("RemWarn command request index arguments");
            user.setExcepted(getCommandName(), "index", InputExpectation.UserInputType.INTEGER);
            output.output(interaction.setLanguageValue("remWarn.index"));
            return;
        }

        com.pengrad.telegrambot.model.User targetMember
                = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"));
        long targetMemberId = targetMember.id();
        User targetUser = interactionTelegram.findUserById(targetMemberId);
        int index = (int) user.getValue(getCommandName(), "index");


        if (interactionTelegram.existsWarningById(interaction.getChatId(), targetMemberId, index)) {
            targetUser.removeWarning(interaction.getChatId(), index);
            interactionTelegram.removeWarning(interaction.getChatId(), targetMemberId, index);
            logger.info(String.format("Removing a warning by id(%s) from a user by id(%s)", index, targetMemberId));
            output.output(interaction.setLanguageValue("remWarn.complete",
                    List.of(targetMember.username(), String.valueOf(index))));
            user.clearExpected(getCommandName());
        } else {
            logger.info("RemWarn command request index arguments");
            user.setExcepted(getCommandName(), "index", InputExpectation.UserInputType.INTEGER);
            output.output(interaction.setLanguageValue("remWarn.index"));
        }
    }
}
