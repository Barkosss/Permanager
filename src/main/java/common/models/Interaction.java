package common.models;

import java.util.List;
import java.util.Map;

public interface Interaction {

    enum Platform {
        CONSOLE,
        TELEGRAM
    }

    enum Type {
        INT,
        STRING,
        DATE,
    }

    Platform getPlatform();

    Interaction setPlatform(Platform platform);

    Interaction setMessage(String message);

    String getMessage();

    boolean getInline();

    Interaction setInline(boolean inline);

    Interaction setArguments(List<String> arguments);

    List<String> getArguments();

    String getInputCommandName();

    Interaction setInputCommandName(String inputCommandName);

    String getInputKey();

    Interaction setInputKey(String inputKey);

    void getValue(String commandName, String key);

    void getValueInt(String commandName, String key);

    Interaction getValueDate(String commandName, String key);

    Map<String, Map<String, String>> getExpectedInput();

    Interaction setValue(Map<String, Map<String, String>> expectedInput);

    void clearExpectedInput(String commandName);
}
