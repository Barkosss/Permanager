package common.iostream;

import common.models.Interaction;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Чтение входных данных с терминала
public class InputTerminal implements Input {
    public Scanner input = new Scanner(System.in);
    public Validate validate = new Validate();

    // Считать строку из терминала
    private String read() {
        return input.nextLine();
    }

    // Считать строку с терминала и вернуть её
    @Override
    public String getString(Interaction interaction) {
        return read();
    }

    // Считать строку с терминала, сплитнуть по разделителю и вернуть массив аргументов
    @Override
    public List<String> getString(Interaction interaction, String separator) {
        return List.of(read().split(separator));
    }

    // Считать строку с терминала, парснуть её в число и вывести, если получилось число, иначе вывести ошибку
    public int getInt(Interaction interaction) {
        String strInteger;

        while(true) {
            strInteger = read();

            Optional<Integer> intValue = validate.isValidInteger(strInteger);
            if (intValue.isPresent()) {
                return intValue.get();
            } else {
                System.out.print("Error. Invalid value. Try again: ");
            }
        }
    }

    // Считать строку с терминала, парснуть её в дату и вывести, если получилось дату, иначе вывести ошибку
    public LocalDate getDate(Interaction interaction) {
        String strDate;

        while(true) {
            strDate = read();

            Optional<LocalDate> dateValue = validate.isValidDate(strDate);
            if (dateValue.isPresent()) {
                return dateValue.get();
            } else {
                System.out.print("Error. Invalid value. Try again: ");
            }
        }
    }
}