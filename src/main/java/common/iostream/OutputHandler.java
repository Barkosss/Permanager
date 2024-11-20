package common.iostream;

import com.pengrad.telegrambot.request.SendMessage;

import common.models.Interaction;
import common.models.InteractionTelegram;

public class OutputHandler implements Output {

    public void output(Interaction interaction) {
        switch(interaction.getPlatform()) {

            case TELEGRAM: {
                InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);
                SendMessage sendMessage = interactionTelegram.getSendMessage();

                // Отправляем сообщение пользователю в Telegram
                interactionTelegram.telegramBot.execute(sendMessage);
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
