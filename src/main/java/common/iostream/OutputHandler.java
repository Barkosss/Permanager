package common.iostream;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import common.models.Interaction;
import common.models.InteractionTelegram;
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
                    interactionTelegram.telegramBot.execute(sendMessage.parseMode(ParseMode.Markdown));
                    logger.debug(String.format("Send message to chatId(%d, userId=%d) with message(\"%s\")",
                            interaction.getChatId(), interaction.getUserId(),
                            interaction.getMessage().trim().replace("\n", " ")));
                } catch (Exception err) {
                    interactionTelegram.telegramBot.execute(new SendMessage(interaction.getChatId(),
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
                } else {
                    System.out.println(interaction.getMessage());
                }
                break;
            }
        }
    }
}
