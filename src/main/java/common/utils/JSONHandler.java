package common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONHandler {
    LoggerHandler logger = new LoggerHandler();

    public Object read(String pathJSON, String keys) {
        String fullPath = String.format("./src/main/resources/%s", pathJSON);
        logger.debug("Attempting to read JSON file from path: " + fullPath);
        logger.debug("Key path to resolve: " + keys);

        try {
            Object object = new JSONParser().parse(new FileReader(fullPath));
            JSONObject jsonObject = (JSONObject) object;

            for (Object key : keys.split("\\.")) {
                logger.debug("Resolving key: " + key);
                try {
                    jsonObject = (JSONObject) jsonObject.get(key);
                    if (jsonObject == null) {
                        logger.warning("Key '" + key + "' is null during object traversal.");
                        return null;
                    }
                } catch (Exception err) {
                    logger.debug("Key '" + key + "' resolved to non-JSONObject. Returning its value.");
                    return jsonObject.get(key);
                }
            }

            logger.debug("Successfully resolved value for keys: " + keys);
            return jsonObject;

        } catch (IOException | ParseException err) {
            logger.error("Failed to read or parse JSON file: " + fullPath + ". Error: " + err.getMessage(), true);
            return null;
        }
    }

    public boolean check(String pathJSON, String keys) {
        try {
            String fullPath = String.format("./src/main/resources/%s", pathJSON);
            logger.debug("Checking existence of key path '" + keys + "' in JSON file: " + fullPath);

            Object object = new JSONParser().parse(new FileReader(fullPath));
            JSONObject jsonObject = (JSONObject) object;
            for (Object key : keys.split("\\.")) {
                logger.debug("Checking key: " + key);
                try {
                    jsonObject = (JSONObject) jsonObject.get(key);
                    if (jsonObject == null) {
                        logger.warning("Key '" + key + "' not found or is null.");
                        return false;
                    }
                } catch (Exception err) {
                    boolean exists = jsonObject.get(key) != null;
                    logger.debug("Reached non-JSONObject value. Existence check result: " + exists);
                    return exists;
                }
            }

            logger.debug("All keys successfully found.");
            return true;
        } catch (Exception err) {
            logger.error("Failed to check key existence in JSON file: " + err.getMessage(), true);
            return false;
        }
    }
}
