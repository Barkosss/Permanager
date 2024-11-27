package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessages;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClearCommand implements BaseCommand {
    Validate validate = new Validate();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "clear";
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
        Optional<Integer> validateInteger = validate.isValidInteger(arguments.getFirst());
        validateInteger.ifPresent(countMessages -> user.setExcepted(getCommandName(), "countMessages").setValue(countMessages));
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
        if (interactionTelegram.getContentReply() != null) {
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        int countMessages = 0;
        List<Integer> messagesIds = new ArrayList<>(List.of());

        // Получаем количество удаляемых сообщений
        if (!user.isExceptedKey(getCommandName(), "countMessages")) {
            user.setExcepted(getCommandName(), "countMessages");
            logger.info("Clear command requested a counter argument");
            output.output(interactionTelegram.setMessage("Enter count messages for delete"));
            return;
        }

        GetUpdates getUpdates = new GetUpdates().limit(100);
        GetUpdatesResponse updatesResponse = interactionTelegram.telegramBot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();

        int maxCountMessages = (Integer) user.getValue(getCommandName(), "countMessages");
        for (Update update : updates) {
            Message message = update.message();

            if (user.getValue(getCommandName(), "user") != null && message.from().id() == interaction.getUserId()) {
                messagesIds.add(message.messageId());
            } else if (user.getValue(getCommandName(), "user") == null) {
                messagesIds.add(message.messageId());
            }
            countMessages++;

            if (countMessages <= maxCountMessages) {
                break;
            }
        }

        int[] arrayMessagesIds = new int[messagesIds.size()];
        for (int i = 0; i < messagesIds.size(); i++) {
            arrayMessagesIds[i] = messagesIds.get(i);
        }
        System.out.println(messagesIds);

        interactionTelegram.telegramBot.execute(new DeleteMessages(interaction.getChatId(), arrayMessagesIds));
    }
}
