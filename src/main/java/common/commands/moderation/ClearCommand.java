package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessages;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import common.commands.BaseCommand;
import common.enums.ModerationCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClearCommand implements BaseCommand {
    ValidateService validate = new ValidateService();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // Получаем длительность блокировки
        Optional<Integer> validateInteger = validate.isValidInteger(arguments.getFirst());
        validateInteger.ifPresent(countMessages ->
                user.setExcepted(getCommandName(), "countMessages").setValue(countMessages));
    }

    @Override
    public void run(Interaction interaction) {

        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue(""));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        if (!user.hasPermission(interaction.getChatId(), ModerationCommand.CLEAR)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        // Получаем количество удаляемых сообщений
        if (!user.isExceptedKey(getCommandName(), "countMessages")) {
            user.setExcepted(getCommandName(), "countMessages", InputExpectation.UserInputType.INTEGER);
            logger.info("Clear command requested a counter argument");
            output.output(interactionTelegram.setMessage("Enter count messages for delete"));
            return;
        }

        long chatId = interaction.getChatId();
        int lastMessageId = (int) interactionTelegram.getChatId();
        int countDeleteMessages = (int) user.getValue(getCommandName(), "countMessages");

        int[] arrayMessagesId = new int[countDeleteMessages];
        for (int index = 0; index < countDeleteMessages; index++) {
            arrayMessagesId[index] = lastMessageId - index;
        }

        interactionTelegram.execute(new DeleteMessages(chatId, arrayMessagesId));
        output.output(interactionTelegram.setLanguageValue(""));
        user.clearExpected(getCommandName());
    }
}
