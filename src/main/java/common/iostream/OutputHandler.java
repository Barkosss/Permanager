package common.iostream;

import com.pengrad.telegrambot.request.SendMessage;

import common.models.InteractionTelegram;

public class OutputHandler implements Output {

    public void output(InteractionTelegram interaction) {
        switch(interaction.getPlatform()) {
            case "terminal": {
                boolean inline = interaction.getInline();
                if (inline) {
                    System.out.print(interaction.getMessage());
                } else {
                    System.out.println(interaction.getMessage());
                }
                break;
            }

            case "telegram": {
                long chatId = interaction.getUserID();
                String message = interaction.getMessage();

                interaction.TELEGRAM_BOT.execute(new SendMessage(chatId, message));
                break;
            }
        }
    }
}
