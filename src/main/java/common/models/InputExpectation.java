package common.models;

import java.util.HashMap;
import java.util.Map;

public class InputExpectation {

    public enum UserInputType {
        STRING,
        DATE,
        INTEGER
    }

    // Название команды, ожидающая ввод
    String expectedCommandName;

    // Какое значение требуется (ключ Map)
    String expectedInputKey;

    // Ожидаемый тип
    UserInputType userInputType;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, Object>> expectedInputs;

    public void setExpected(String expectedCommandName, String expectedInputKey, UserInputType userInputType) {
        if (this.expectedInputs == null) {
            this.expectedInputs = new HashMap<>();
        }

        Map<String, Object> excepted = this.expectedInputs.get(expectedCommandName);
        if (excepted == null) {
            this.expectedInputs.put(expectedCommandName, new HashMap<>());
            excepted = this.expectedInputs.get(expectedCommandName);
        }

        if (excepted.containsKey(expectedInputKey)) {
            excepted.put(expectedCommandName, null);
        }

        this.expectedCommandName = expectedCommandName;
        this.expectedInputKey = expectedInputKey;
        this.userInputType = userInputType;
    }

    public Map<String, Map<String, Object>> getExpectedInputs() {
        if (expectedInputs == null) {
            expectedInputs = new HashMap<>();
        }
        return expectedInputs;
    }
}