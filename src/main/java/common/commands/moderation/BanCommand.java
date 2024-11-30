package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
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
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // Получаем длительность блокировки
        Optional<LocalDate> durationDate = validate.isValidDate(arguments.get(arguments.size() - 2)
                + " " + arguments.getLast());
        durationDate.ifPresent(localDate -> user.setExcepted(getCommandName(), "duration").setValue(localDate));

        // Получаем причину блокировки
        if (arguments.size() > 1) {
            user.setExcepted(getCommandName(), "reason").setValue(String.join(" ",
                    arguments.subList(0, arguments.size() - 1)));
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        if (interactionTelegram.telegramBot.execute(new GetChatMemberCount(interaction.getChatId())).count() <= 2) {
            output.output(interaction.setMessage("This command is not available for private chat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Ban command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /ban"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            // Валидация пользователя...
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Ban command requested a reason argument");
            output.output(interactionTelegram.setMessage("Enter reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Ban command requested a duration argument");
            output.output(interactionTelegram.setMessage("Enter duration"));
            return;
        }
        // Валидация даты...

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been banned");
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            output.output(interactionTelegram.setMessage(String.format("The user @%s has been banned to %s with reason: %s",
                    username, user.getValue(getCommandName(), "duration"), user.getValue(getCommandName(), "reason"))));
        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Ban command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
