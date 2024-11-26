package common.iostream;

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
                interactionTelegram.telegramBot.execute(sendMessage);
                logger.debug("Send message to chatId(" + interaction.getChatId()
                        + ", userId=" + interaction.getUserId()
                        + ") with message(\"" + interaction.getMessage().trim().replace("\n", " ") + "\")");
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
