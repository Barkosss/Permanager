package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.models.Interaction;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InputTelegram /*implements Input*/ {
    /*
    public Output outputTelegram = new OutputTelegram();
    public Validate validate = new Validate();

    // Считать строку сообщения из телеграмма
    private String read() {
        return input.get();
    }

    @Override
    public String getString() {
        String message = "";
        Interaction.TELEGRAM_BOT.removeGetUpdatesListener();
        return message;
    }

    @Override
    public List<String> getString(String separator) {
        String message = read();
        Interaction.TELEGRAM_BOT.removeGetUpdatesListener();
        return List.of(message.split(separator));
    }

    @Override
    public int getInt() {
        String strInteger;

        while(true) {
            strInteger = read();

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
                Interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return intValue.get();
            } else {
                outputTelegram.output(new Interaction("Error. Invalid value. Try again: "),false);
            }
        }
    }

    @Override
    public LocalDate getDate() {
        String strDate;

        while (true) {
            strDate = read();

            Optional<LocalDate> dateValue = validate.isValidDate(strDate);
            if (dateValue.isPresent()) {
                Interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return dateValue.get();
            } else {
                outputTelegram.output(new Interaction("Error. Invalid value. Try again: "), false);
            }
        }
    }
    */
}
