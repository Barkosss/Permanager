package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.Server;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BanCommand implements BaseCommand {
    Validate validate = new Validate();
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

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        if (arguments.size() >= 2) {
            // Получаем длительность блокировки
            Optional<LocalDate> durationDate = validate.isValidDate(String.format("%s %s",
                    arguments.get(arguments.size() - 2), arguments.getLast()));

            if (durationDate.isPresent()) {
                user.setExcepted(getCommandName(), "duration").setValue(durationDate.get());
                arguments.subList(0, arguments.size() - 2);
            }
        }

        // Получаем причину блокировки
        if (arguments.size() > 1) {
            user.setExcepted(getCommandName(), "reason").setValue(String.join(" ",
                    arguments.subList(0, arguments.size() - 1)));
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

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.BAN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        if (interactionTelegram.telegramBot.execute(new GetChatMemberCount(interaction.getChatId())).count() <= 2) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Ban command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("ban.replyMessage"));
            return;
        }
        user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());

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
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info(String.format("User by id(%s) in chat by id(%s) has been banned",
                    userId, interaction.getChatId()));

            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            interactionTelegram.getServerRepository().findById(interaction.getChatId()).addUserBan(user);
            output.output(interaction.setLanguageValue("ban.complete",
                    List.of(username, (String) user.getValue(getCommandName(), "duration"),
                            (String) user.getValue(getCommandName(), "reason"))));

        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error(String.format("Ban command: %s", err));
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
