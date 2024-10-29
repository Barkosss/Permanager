package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.CommandHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;

import java.util.ArrayList;
import java.util.List;

public class InputTelegram {

    public void read(Interaction interaction) {
        CommandHandler commandHandler = new CommandHandler();
        List<common.models.Update> smallUpdates = new ArrayList<>();

        // Обработка всех изменений
        ((InteractionTelegram)interaction).TELEGRAM_BOT.setUpdatesListener(updates -> {
            common.models.Update smallUpdate = new common.models.Update();

            for(Update update : updates) {
                smallUpdates.add(smallUpdate.create(update));
            }

            commandHandler.getCommandTelegram(interaction, smallUpdates);

            System.out.println("Updates listener confirmed updates all");

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));
    }
}