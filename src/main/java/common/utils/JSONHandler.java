package common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONHandler {

    public Object read(String pathJSON, String keys) {
        try {
            Object object = new JSONParser().parse(new FileReader(pathJSON));
            JSONObject jsonObject = (JSONObject) object;

            return jsonObject.get(keys);
        } catch(IOException | ParseException err) {
            System.out.println("[ERROR] JSONHandler: " + err);
            return new Object();
        }
    }

    public void write(String pathJNSON, String keys) {

    }
}