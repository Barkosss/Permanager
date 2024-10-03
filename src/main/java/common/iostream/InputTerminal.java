package common.iostream;

import common.utils.Validate;

import java.util.Date;
import java.util.Scanner;

// Чтение входных данных с терминала
public class InputTerminal implements Input {
    public Scanner input = new Scanner(System.in);
    public Validate validate = new Validate();

    private String read() {
        return input.nextLine();
    }

    @Override
    public String getString() {
        return read();
    }

    public int getInt() {
        String strInteger;

        while(true) {
            strInteger = read();

            int intValue = validate.isValidInteger(strInteger);
            if (intValue != Integer.parseInt(null)) {
                return intValue;
            } else {
                System.out.print("Error. Invalid value. Try again: ");
            }
        }
    }

    public Date getDate() {
        String strDate;

        while(true) {
            strDate = read();

            Date dateValue = validate.isValidDate(strDate);
            if (dateValue.equals(null)) {
                return dateValue;
            } else {
                System.out.print("Error. Invalid value. Try again: ");
            }
        }
    }
}