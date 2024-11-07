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

                if (sendMessage == null) {
                    long chatId = interactionTelegram.getUserID();
                    String message = interactionTelegram.getMessage();
                    sendMessage = interactionTelegram.setSendMessage(new SendMessage(chatId, message)).getSendMessage();
                }

                interactionTelegram.TELEGRAM_BOT.execute(sendMessage);
                ((InteractionTelegram) interaction).setSendMessage(null);
                break;
            }

            case CONSOLE: {}
            default: {
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
