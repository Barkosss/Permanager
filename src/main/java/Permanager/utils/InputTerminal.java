package utils;

import exceptions.*;

import java.util.Date;
import java.util.Scanner;

// Чтение входных данных с терминала
public class InputTerminal {
    public boolean flag;
    public static Scanner input = new Scanner(System.in);
    public static Validate validate = new Validate();

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    private String read() {
        return input.nextLine();
    }

    public String getString() throws WrongArgumentsException {
        return read();
    }

    public int getInt() throws WrongArgumentsException {
        int result = 0;
        String strInteger = "";

        while(true) {
            strInteger = read();
            if (validate.isValidInteger(strInteger)) {
                result = Integer.getInteger(strInteger);
                break;
            }
        }

        return result;
    }

    public Date getDate() {
        Date result = new Date();
        String strDate = "";

        while(true) {
            strDate = read();
            if (validate.isValidDate(strDate)) {
                result = new Date();
                break;
            }
        }

        return result;
    }
}