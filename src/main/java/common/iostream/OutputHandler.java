package common.iostream;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Server;
import common.utils.LoggerHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OutputHandler {
    LoggerHandler logger = new LoggerHandler();

    public void output(Interaction interaction) {
        logger.debug("Processing output for platform: " + interaction.getPlatform());

        switch (interaction.getPlatform()) {
            case TELEGRAM -> handlerTelegramOutput((InteractionTelegram) interaction);
            case CONSOLE -> handlerConsoleOutput(interaction);
        }
    }

    public void handlerTelegramOutput(InteractionTelegram interaction) {
        logger.debug("Preparing to send message to Telegram.");
        SendMessage sendMessage = interaction.getSendMessage();

        // Если объект не создан, то принудительно выйти, то есть не отправить сообщение
        if (sendMessage == null) {
            logger.debug("SendMessage was null. Aborting output.");
            return;
        }

        // Отправляем сообщение пользователю в Telegram
        try {
            sendMessage.parseMode(ParseMode.Markdown)
                    .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true));

            logger.debug("Sending message with formatting to chat ID: " + interaction.getChatId());
            SendResponse sendRequest = interaction.execute(sendMessage);
            Server server = interaction.findServerById(interaction.getChatId());

            // Если не получилось отправить сообщение после парсинга стиля
            if (!sendRequest.isOk()) {
                logger.warning("Failed to send message with formatting. Retrying without formatting.");
                SendMessage request = new SendMessage(interaction.getChatId(), interaction.getMessage())
                        .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true));
                sendRequest = interaction.execute(request);

                logger.debug(String.format("The message was sent to the chat by id(%s) without formatting",
                        interaction.getChatId()));
            } else {
                logger.debug(String.format("The message was sent to the chat by id(%s) with formatting",
                        interaction.getChatId()));
            }

            int messageId = (sendRequest.message() != null) ? (sendRequest.message().messageId()) : -1;

            // Удалить сообщение через время
            if (messageId != -1) {
                logger.debug("Scheduling message deletion for ID: " + messageId);
                scheduleMessageDeleter(interaction.getTelegramBot(), server,
                        interaction.getChatId(), messageId, interaction.getOutputStatus());
            } else {
                logger.debug("Message ID is invalid or not returned.");
            }

        } catch (Exception err) {
            logger.error("Exception occurred while sending message to chat ID(" +
                    interaction.getChatId() + "): " + err.getMessage(), true);
            interaction.execute(new SendMessage(interaction.getChatId(),
                    interaction.getLanguageValue("system.error.something")));
        }
    }

    public void handlerConsoleOutput(Interaction interaction) {
        String message = interaction.getMessage();
        if (interaction.getInline()) {
            System.out.print(message);
            logger.debug("Console output (inline): %s" + message);
        } else {
            System.out.println(message);
            logger.debug("Console output (newline): %s" + message);
        }
    }

    private void scheduleMessageDeleter(TelegramBot telegramBot, Server server, long chatId,
                                        int messageId, InteractionTelegram.OutputStatus status) {
        long durationDeleteMessage = server.getDurationDeleteMessage(status);

        if (durationDeleteMessage == 0) {
            logger.debug("Message deletion is disabled (duration = 0).");
            return;
        }

        logger.debug(String.format("Scheduling deletion for message by id(%s) after %s seconds ", messageId, durationDeleteMessage));

        // Удаление успешное сообщение через время
        try (ScheduledExecutorService schedulerDeleteMessage = Executors.newSingleThreadScheduledExecutor()) {
            schedulerDeleteMessage.schedule(() -> {
                try {
                    telegramBot.execute(new DeleteMessage(chatId, messageId));
                    logger.debug("Deleted message with ID: " + messageId);
                } catch (Exception e) {
                    logger.error("Failed to delete message: " + e.getMessage());
                }
            }, durationDeleteMessage, TimeUnit.SECONDS);
        } catch (Exception err) {
            logger.error("Failed to schedule message deletion: " + err.getMessage());
        }
    }
}
