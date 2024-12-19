package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.request.BanChatMember;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BanCommand implements BaseCommand {
    ValidateService validate = new ValidateService();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public String getCommandDescription() {
        return "Блокировка пользователя";
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
            user.setExcepted(getCommandName(), "user")
                    .setValue(interactionTelegram.telegramBot
                            .execute(new GetChatMember(interaction.getChatId(), validUserId.get()))
                            .chatMember().user());
            arguments = arguments.subList(1, arguments.size());

        } else if (interactionTelegram.getContentReply() != null) {
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply().from());
        }

        if (arguments.isEmpty()) {
            return;
        }

        if (arguments.size() > 1) {
            Optional<LocalDate> validDate = validate.isValidDate(String.format("%s %s",
                    arguments.getFirst(), arguments.get(1)));
            Optional<LocalDate> validTime = validate.isValidTime(arguments.getFirst());

            if (validDate.isPresent()) {
                user.setExcepted(getCommandName(), "duration").setValue(validDate.get());
                arguments = arguments.subList(2, arguments.size());
            } else if (validTime.isPresent()) {
                user.setExcepted(getCommandName(), "duration").setValue(validTime.get());
                arguments = arguments.subList(1, arguments.size());
            }
        }

        if (arguments.isEmpty()) {
            return;
        }

        // Получаем причину блокировки
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

        if (interactionTelegram.telegramBot.execute(new GetChat(interaction.getChatId())).chat().type()
                == ChatFullInfo.Type.Private) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.BAN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied",
                    List.of(interactionTelegram.getUsername())));
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        if (!user.isExceptedKey(getCommandName(), "user")) {
            Optional<Long> validUserId = validate.isValidLong(interaction.getArguments().getFirst());

            if (interactionTelegram.getContentReply() != null) {
                user.setExcepted(getCommandName(), "user")
                        .setValue(interactionTelegram.getContentReply().from());
            } else if (validUserId.isPresent()) {
                user.setExcepted(getCommandName(), "user")
                        .setValue(interactionTelegram.telegramBot
                                .execute(new GetChatMember(interaction.getChatId(), validUserId.get()))
                                .chatMember().user());
            } else {
                user.setExcepted(getCommandName(), "user", InputExpectation.UserInputType.INTEGER);
                logger.info("Ban command requested a userId arguments");
                output.output(interaction.setLanguageValue("ban.userId"));
                return;
            }
        }

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Ban command requested a reason argument");
            output.output(interactionTelegram.setLanguageValue("ban.reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration", InputExpectation.UserInputType.DATE);
            logger.info("Ban command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("ban.duration"));
            return;
        }

        try {
            long userId = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user")).id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            logger.info(String.format("User by id(%s) in chat by id(%s) has been banned",
                    userId, interaction.getChatId()));

            String username = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"))
                    .username();
            interactionTelegram.findServerById(interaction.getChatId()).addUserBan(user);
            output.output(interaction.setLanguageValue("ban.complete",
                    List.of(username, (String) user.getValue(getCommandName(), "duration"),
                            (String) user.getValue(getCommandName(), "reason"))));

        } catch (Exception err) {
            logger.error(String.format("Something error in Ban command: %s", err));
            output.output(interaction.setLanguageValue("system.error.something"));
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
