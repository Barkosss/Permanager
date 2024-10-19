package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.commands.BaseCommand;
import common.models.Interaction;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;


public class InputHandler implements Input {
    public Map<String, BaseCommand> baseCommandClasses;
    public Scanner scanner = new Scanner(System.in);
    public Output output = new OutputHandler();
    public Validate validate = new Validate();

    public InputHandler() {}

    public String read(Interaction interaction) {
        return scanner.nextLine();
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
        //interaction.TELEGRAM_BOT.removeGetUpdatesListener();
        return List.of(message.split(separator));
    }

    @Override
    public int getInt(Interaction interaction) {
        String strInteger;

        while(true) {
            strInteger = read(interaction);

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
                //interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return intValue.get();
            } else {
                output.output(interaction.setMessage("Error. Invalid value. Try again: "));
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
                //interaction.TELEGRAM_BOT.removeGetUpdatesListener();
                return dateValue.get();
            } else {
                output.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }
}
