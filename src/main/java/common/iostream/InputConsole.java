package common.iostream;

import common.models.Interaction;
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
    public List<String> getString(Interaction interaction, String separator) {
        return List.of(read().split(separator));
    }

    @Override
    public int getInt(Interaction interaction) {
        String strInteger;

        while(true) {
            strInteger = read();

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
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
            strDate = read();

            Optional<LocalDate> dateValue = validate.isValidDate(strDate);
            if (dateValue.isPresent()) {
                return dateValue.get();
            } else {
                output.output(interaction.setMessage("Error. Invalid value. Try again: "));
            }
        }
    }
}
