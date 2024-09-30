package common.iostream;

import common.utils.Validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

// Чтение входных данных с терминала
public class InputTerminal implements Input {
    public static Scanner input = new Scanner(System.in);
    public static Validate validate = new Validate();

    private String read() {
        return input.nextLine();
    }

    public String getString() {
        return read();
    }

    public ArrayList<String> getStrings(int countElements) {
        ArrayList<String> arrayElements = new ArrayList<>();
        for(int index = 0; index < countElements; index++) {
            arrayElements.add(read());
        }

        return arrayElements;
    }

    public int getInt() {
        int result;
        String strInteger;

        while(true) {
            strInteger = read();
            if (validate.isValidInteger(strInteger)) {
                result = Integer.parseInt(strInteger);
                break;
            }
        }

        return result;
    }

    public Date getDate() {
        Date result;
        String strDate;

        while(true) {
            strDate = read();
            if (validate.isValidDate(strDate)) {
                result = new Date(); // <-- Изменить
                break;
            }
        }

        return result;
    }
}