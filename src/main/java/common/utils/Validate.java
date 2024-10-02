package common.utils;

import java.text.SimpleDateFormat;

public class Validate {

    // Валидация числа
    public boolean isValidInteger(String integer) {

        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Валидация даты
    public boolean isValidDate(String date) {

        String[] patterns = {
                "HH:mm dd.MM.YYYY",
                "HH:mm:ss dd.MM.YYYY",
                "HH:mm dd.MM.YY",
                "HH:mm:ss dd.MM.YY"
        };

        for(String pattern : patterns) {
            try {
                System.out.println(date + ": " + pattern);
                new SimpleDateFormat(pattern).parse(date);
                return true;
            } catch(Exception err) {
                continue;
            }
        }
        return false;
    }
}
