package common.utils;

import java.text.SimpleDateFormat;
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
    public Date isValidDate(String date) {

        String[] patterns = {
                "H:mm dd.MM.yyyy",
                "H:mm:ss dd.MM.yyyy",
                "H:mm dd.MM.yy",
                "H:mm:ss dd.MM.yy"
        };

        for(String pattern : patterns) {
            try {
                Optional<Date> localDateValue = Optional.of(new SimpleDateFormat(pattern).parse(date));
                return localDateValue.get();
            } catch(Exception err) {
                continue;
            }
        }
        return null;
    }
}
