package common.iostream;

import com.pengrad.telegrambot.request.SendMessage;
import common.models.Interaction;

import static common.models.Interaction.TELEGRAM_BOT;

public class OutputTelegram implements Output {

    @Override
    public void output(Interaction interaction, boolean inline) {
        long chatId = interaction.getUserID();
        String message = interaction.getMessage();

        TELEGRAM_BOT.execute(new SendMessage(chatId, message));
    }
}
