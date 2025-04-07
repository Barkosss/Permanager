package common.iostream;

import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.DeleteMessages;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Server;
import common.utils.LoggerHandler;

public class OutputHandler {
    LoggerHandler logger = new LoggerHandler();

    public void output(Interaction interaction) {
        switch (interaction.getPlatform()) {

            case TELEGRAM: {
                InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);
                SendMessage sendMessage = interactionTelegram.getSendMessage();

                // Если объект не создан, то принудительно выйти, то есть не отправить сообщение
                if (sendMessage == null) {
                    return;
                }

                // Отправляем сообщение пользователю в Telegram
                try {
                    SendResponse sendRequest = interactionTelegram
                            .execute(sendMessage.parseMode(ParseMode.Markdown)
                                    .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)));
                    int messageId = sendRequest.message().messageId();
                    Server server = interactionTelegram.findServerById(interactionTelegram.getChatId());

                    // Если не получилось отправить сообщение после парсинга стиля
                    if (!sendRequest.isOk()) {
                        interactionTelegram.execute(new SendMessage(interaction.getChatId(),
                                interaction.getMessage())
                                .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)));
                        logger.debug(String.format("The message was sent to the chat by id(%s) without formatting", interactionTelegram.getChatId()));
                    } else {
                        logger.debug(String.format("The message was sent to the chat by id(%s) with formatting", interactionTelegram.getChatId()));
                    }

                    // Удаление успешное сообщение через время
                    if (server.getDurationDeleteSuccessfulMessage() > 0) {
                        interactionTelegram.execute(new DeleteMessage(interactionTelegram.getChatId(),messageId));
                    }

                } catch (Exception err) {
                    interactionTelegram.execute(new SendMessage(interaction.getChatId(),
                            interaction.getLanguageValue("system.error.something")));
                    logger.error(String.format("I couldn't send a message in the chat by id(%s): %s",
                            interactionTelegram.getChatId(), err));
                }
                break;
            }

            case CONSOLE: {
                boolean inline = interaction.getInline();
                if (inline) {
                    System.out.print(interaction.getMessage());
                    logger.debug(String.format("Console output (inline): %s", interaction.getMessage()));
                } else {
                    System.out.println(interaction.getMessage());
                    logger.debug(String.format("Console output (newline): %s", interaction.getMessage()));
                }
                break;
            }
        }
    }
}
