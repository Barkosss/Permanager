package common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class Validate {

    // Optional от Int

    // Валидация числа
    public Integer isValidInteger(String strInteger) {

        Optional<Integer> intValue = Optional.of(Integer.parseInt(strInteger));
        return intValue.orElse((null));
    }

    // Валидация даты
    public LocalDate isValidDate(String input) {

        String[] patterns = {
                "H:mm dd.MM.yyyy",
                "H:mm:ss dd.MM.yyyy",
                "H:mm dd.MM.yy",
                "H:mm:ss dd.MM.yy"
        };

        for(String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate date = LocalDate.parse(input, formatter);
                System.out.println("LocalDate: " + date);
                return date;
            } catch(Exception err) {
                continue;
            }
        }
        return null;
    }
}
