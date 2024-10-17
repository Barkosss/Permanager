package common.iostream;

import com.pengrad.telegrambot.request.SendMessage;
import common.models.Interaction;

public class OutputTelegram implements Output {

    @Override
    public void output(Interaction interaction) {
        long chatId = interaction.getUserID();
        String message = interaction.getMessage();

        interaction.TELEGRAM_BOT.execute(new SendMessage(chatId, message));
    }
}
