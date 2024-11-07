package common.models;

import java.util.Map;

public class InputExpectation {

    // Какой тип ожидается от пользователя
    Interaction.UserInputType userInputType;

    // Название команды, ожидающая ввод
    String expectedCommandName;

    // Какое значение требуется (ключ Map)
    String expectedInputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInputs;

    public String getExpectedCommandName() {
        return expectedCommandName;
    }

    public InputExpectation setExpectedCommandName(String expectedCommandName) {
        this.expectedCommandName = expectedCommandName;
        return this;
    }

    public String getExpectedInputKey() {
        return expectedInputKey;
    }

    public InputExpectation setExpectedInputKey(String expectedInputKey) {
        this.expectedInputKey = expectedInputKey;
        return this;
    }

    public void getValue(String commandName, String key) {
        setExpectedCommandName(commandName);
        setExpectedInputKey(key);
        this.userInputType = Interaction.UserInputType.STRING;
    }

    public Map<String, Map<String, String>> getExpectedInputs() {
        return expectedInputs;
    }

    public InputExpectation setValue(Map<String, Map<String, String>> expectedInput) {
        this.expectedInputs = expectedInput;
        return this;
    }

    public void clearExpectedInput(String commandName) {
        expectedInputKey = expectedCommandName = null;
        userInputType = null;
        expectedInputs.get(commandName).clear();
    }
}