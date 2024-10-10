package common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * Обработчик JSON файла: Чтение и Запись
 */
public class JSONHandler {

    public JSONObject commandObject;

    // Метод для редактирования значения по ключу
    void edit(String pathJSON, String keys, Object value) {
        try {
            commandObject = (JSONObject)new JSONParser().parse(new FileReader(pathJSON))    ;
        } catch (IOException | ParseException err) {
            throw new RuntimeException(err);
        }

        // ...
    }

    // Метод для чтения значения по ключу
    Object read(String pathJSON, String keys) {
        try {
            commandObject = (JSONObject)new JSONParser().parse(new FileReader(pathJSON));
        } catch (IOException | ParseException err) {
            throw new RuntimeException(err);
        }

        // ...

        return "";
    }
}