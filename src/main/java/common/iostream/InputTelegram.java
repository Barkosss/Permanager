package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.models.InteractionTelegram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InputTelegram implements Input {

    public List<common.models.Update> read(InteractionTelegram interaction) {
        List<common.models.Update> smallUpdates = new ArrayList<>();

        // Обработка всех изменений
        interaction.TELEGRAM_BOT.setUpdatesListener(updates -> {
            common.models.Update smallUpdate = new common.models.Update();

            for(Update update : updates) {
                smallUpdates.add(smallUpdate.create(update));
            }

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));

        return smallUpdates;
    }

    public List<common.models.Update> getUpdates(InteractionTelegram interaction) {
        return read(interaction);
    }

    @Override
    public String getString() {
        return "";
    }

    @Override
    public List<String> getString(InteractionTelegram interaction, String separator) {
        return List.of();
    }

    @Override
    public int getInt(InteractionTelegram interaction) {
        return 0;
    }

    @Override
    public LocalDate getDate(InteractionTelegram interaction) {
        return null;
    }
}
