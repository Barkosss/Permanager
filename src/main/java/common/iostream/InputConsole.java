package common.iostream;

import common.models.AbstractInteraction;
import common.models.InteractionTelegram;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class InputConsole implements Input {
    public Scanner scanner = new Scanner(System.in);
    public Output output = new OutputHandler();
    public Validate validate = new Validate();

    public String read() {
        return scanner.nextLine();
    }

    @Override
    public String getString() {
        return read();
    }

    @Override
    public List<String> getString(InteractionTelegram interaction, String separator) {
        String message = read(interaction);
        return List.of(message.split(separator));
    }

    @Override
    public int getInt(InteractionTelegram interaction) {
        String strInteger;

        while(true) {
            strInteger = read(interaction);

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
                return intValue.get();
            } else {
                output.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }

    @Override
    public LocalDate getDate(InteractionTelegram interaction) {
        String strDate;

        while (true) {
            strDate = read(interaction);

            Optional<LocalDate> dateValue = validate.isValidDate(strDate);
            if (dateValue.isPresent()) {
                return dateValue.get();
            } else {
                output.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }
}
