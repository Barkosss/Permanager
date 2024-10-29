package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;

import common.CommandHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;

import java.util.List;

public class InputTelegram {

    public void read(Interaction interaction, CommandHandler commandHandler) {

        // Обработка всех изменений
        ((InteractionTelegram)interaction).TELEGRAM_BOT.setUpdatesListener(updates -> {
            common.models.Update smallUpdate = new common.models.Update();

            commandHandler.getCommandTelegram(interaction, List.of(smallUpdate.create(updates.getLast())));

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));
    }
}