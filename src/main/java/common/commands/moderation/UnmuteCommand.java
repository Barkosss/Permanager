package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.RestrictChatMember;
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

public class UnmuteCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "unmute";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        Optional<Long> validUserId = validate.isValidLong(arguments.getFirst());
        if (validUserId.isPresent()) {
            logger.debug(String.format("User by id(%s) is valid", validUserId.get()));
            user.setExcepted(getCommandName(), "user")
                    .setValue(interactionTelegram.telegramBot
                            .execute(new GetChatMember(interaction.getChatId(), validUserId.get()))
                            .chatMember().user());
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Проверяем на приватность чата
        if (interactionTelegram.telegramBot
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    interaction.getUserId(), getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), ModerationCommand.MUTE)) {
            try {
                output.output(interaction.setLanguageValue("system.error.accessDenied", List.of(
                        interactionTelegram.getUsername()
                )));
            } catch (Exception err) {
                logger.error(String.format("Error in command (%s): %s", getCommandName(), err));
                output.output(interaction.setLanguageValue("system.error.accessDenied"));
            }
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Mute command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("mute.replyMessage"));
            return;
        }

        // Получаем пользователя из ответного сообщения
        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            Message content = interactionTelegram.getContentReply();

            // Если пользователь это бот или взаимодействующий
            if (content.from().isBot() || content.from().id() == interaction.getUserId()) {
                user.setExcepted(getCommandName(), "user");
                output.output(interaction.setLanguageValue("warn.replyMessage"));
                return;
            }

            user.setExcepted(getCommandName(), "user").setValue(content.from());
        }

        try {
            com.pengrad.telegrambot.model.User targetMember
                    = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"));
            long targetMemberId = targetMember.id();
            interactionTelegram.telegramBot.execute(new RestrictChatMember(interaction.getChatId(), targetMemberId,
                    new ChatPermissions().canSendMessages(true)));
            User targetUser = interactionTelegram.findUserById(targetMemberId);
            interactionTelegram.findServerById(interaction.getChatId()).removeUserMute(targetUser);
            logger.info(String.format("User by id(%s) in chat by id(%s) has been unmuted",
                    targetMemberId,
                    interaction.getChatId()));
            output.output(interactionTelegram.setLanguageValue("unmute.complete",
                    List.of(targetMember.username())));
        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Unmute command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
