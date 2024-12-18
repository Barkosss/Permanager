package common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONHandler {

    public Object read(String pathJSON, String keys) {
        try {
            Object object = new JSONParser().parse(new FileReader("./src/main/resources/" + pathJSON));
            JSONObject jsonObject = (JSONObject) object;
            for (Object key : keys.split("\\.")) {
                try {
                    jsonObject = (JSONObject) jsonObject.get(key);
                } catch (Exception err) {
                    return jsonObject.get(key);
                }
            }
            return jsonObject;
        } catch (IOException | ParseException err) {
            System.out.println("[ERROR] JSONHandler: " + err);
            return new Object();
        }
    }

    public boolean check(String pathJSON, String keys) {
        try {
            Object object = new JSONParser().parse(new FileReader("./src/main/resources/" + pathJSON));
            JSONObject jsonObject = (JSONObject) object;
            for (Object key : keys.split("\\.")) {
                try {
                    jsonObject = (JSONObject) jsonObject.get(key);
                } catch (Exception err) {
                    return jsonObject != null;
                }
            }
            return jsonObject != null;
        } catch (Exception err) {
            return false;
        }
    }
}