package common.iostream;

import com.pengrad.telegrambot.request.SendMessage;

import common.models.Interaction;
import common.models.InteractionConsole;
import common.models.InteractionTelegram;

public class OutputHandler implements Output {

    public void output(Interaction interaction) {
        switch(interaction.getPlatform()) {
            case CONSOLE: {
                InteractionConsole interactionConsole = ((InteractionConsole) interaction);
                boolean inline = interactionConsole.getInline();
                if (inline) {
                    System.out.print(interactionConsole.getMessage());
                } else {
                    System.out.println(interactionConsole.getMessage());
                }
                break;
            }

            case TELEGRAM: {
                InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);
                long chatId = interactionTelegram.getUserID();
                String message = interactionTelegram.getMessage();

                interactionTelegram.TELEGRAM_BOT.execute(new SendMessage(chatId, message));
                break;
            }
        }
    }
}
