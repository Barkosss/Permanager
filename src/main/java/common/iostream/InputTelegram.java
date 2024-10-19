package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.models.Interaction;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InputTelegram implements Input {
    public Output outputTelegram = new OutputHandler();
    public Validate validate = new Validate();

    // Считать строку сообщения из телеграмма
    public String read(Interaction interaction) {
        AtomicReference<String> input = new AtomicReference<>();

        // Register for updates
        interaction.TELEGRAM_BOT.setUpdatesListener(updates -> {
            Update update = updates.getFirst();

            if (update.message() == null) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }

            input.set(update.message().text());

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));

        return input.get();
    }

    @Override
    public String getString(Interaction interaction) {
        String message = read(interaction);
        interaction.TELEGRAM_BOT.removeGetUpdatesListener();
        return message;
    }

    @Override
    public List<String> getString(Interaction interaction, String separator) {
        String message = read(interaction);
        interaction.TELEGRAM_BOT.removeGetUpdatesListener();
        return List.of(message.split(separator));
    }

    @Override
    public int getInt(Interaction interaction) {
        String strInteger;

        while(true) {
            strInteger = read(interaction);

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
                interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return intValue.get();
            } else {
                outputTelegram.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }

    @Override
    public LocalDate getDate(Interaction interaction) {
        String strDate;

        while (true) {
            strDate = read(interaction);

            Optional<LocalDate> dateValue = validate.isValidDate(strDate);
            if (dateValue.isPresent()) {
                interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return dateValue.get();
            } else {
                outputTelegram.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }
}