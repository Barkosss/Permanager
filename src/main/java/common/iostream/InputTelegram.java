package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.models.Interaction;
import common.models.InteractionTelegram;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InputTelegram implements Input {
    public Output output = new OutputHandler();
    public Validate validate = new Validate();

    public List<common.models.Update> read(Interaction interaction) {
        List<common.models.Update> smallUpdates = new ArrayList<>();

        // Обработка всех изменений
        ((InteractionTelegram)interaction).TELEGRAM_BOT.setUpdatesListener(updates -> {
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

    public List<common.models.Update> getUpdates(Interaction interaction) {
        return read(interaction);
    }

    @Override
    public String getString(Interaction interaction) {
        return read(interaction).getFirst().getMessage();
    }

    @Override
    public List<String> getString(Interaction interaction, String separator) {
        List<String> messages = new ArrayList<>();
        List<common.models.Update> updates = read(interaction);

        for(common.models.Update update : updates) {
            messages.add(update.getMessage());
        }

        return messages;
    }

    @Override
    public int getInt(Interaction interaction) {
        String strInteger;

        while(true) {
            List<common.models.Update> updates = read(interaction);

            for(common.models.Update update : updates) {
                strInteger = update.getMessage();

                Optional<Integer> intValue = validate.isValidInteger(strInteger);
                if (intValue.isPresent()) {
                    return intValue.get();
                } else {
                    output.output(interaction.setMessage("Error. Invalid value. Try again"));
                }
            }
        }
    }

    @Override
    public LocalDate getDate(Interaction interaction) {
        String strDate;

        while (true) {
            List<common.models.Update> updates = read(interaction);

            for(common.models.Update update : updates) {
                strDate = update.getMessage();

                Optional<LocalDate> dateValue = validate.isValidDate(strDate);
                if (dateValue.isPresent()) {
                    return dateValue.get();
                } else {
                    output.output(interaction.setMessage("Error. Invalid value. Try again"));
                }
            }
        }
    }
}
