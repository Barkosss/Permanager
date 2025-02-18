package common.models;

import java.util.HashMap;
import java.util.Map;

public class InputExpectation {

    enum UserInputType {
        INT,
        STRING,
        DATE,
    }

    // Какой тип ожидается от пользователя
    UserInputType userInputType;

    // Название команды, ожидающая ввод
    String expectedCommandName;

    // Какое значение требуется (ключ Map)
    String expectedInputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInputs;

    public void setExpected(String expectedCommandName, String expectedInputKey) {
        if (!this.expectedInputs.containsKey(expectedCommandName)) {
            expectedInputs.put(expectedCommandName, new HashMap<>());
        }
        if (!this.expectedInputs.get(expectedCommandName).containsKey(expectedInputKey)) {
            expectedInputs.get(expectedCommandName).put(expectedCommandName, null);
        }
        this.expectedCommandName = expectedCommandName;
        this.expectedInputKey = expectedInputKey;
    }

    public Map<String, Map<String, String>> getExpectedInputs() {
        if (expectedInputs == null) {
            expectedInputs = new HashMap<>();
        }
        return expectedInputs;
    }
}