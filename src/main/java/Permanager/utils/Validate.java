package utils;

import java.text.SimpleDateFormat;

public class Validate {

    public static void main(String[] args) {
        System.out.println("Class Validate");
    }

    public boolean isValidDate(String date) {
        // НЕ РАБОТАЕТ
        try {
            new SimpleDateFormat("k:mm  dd.MM.yyyy").parse(date);
            return true;
        } catch(Exception err) {
            return false;
        }
    }

    public boolean isValidInteger(String integer) {
        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
