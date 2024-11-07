package common.models;

import java.util.Map;

public class InputExpectation {

    // Какой тип ожидается от пользователя
    Interaction.UserInputType userInputType;

    // Название команды
    String inputCommandName;

    // Какое значение требуется (ключ Map)
    String inputKey;

    // Map значений, которые указываются пользователем
    Map<String, Map<String, String>> expectedInput;

    public String getInputCommandName() {
        return inputCommandName;
    }

    public InputExpectation setInputCommandName(String inputCommandName) {
        this.inputCommandName = inputCommandName;
        return this;
    }

    public String getInputKey() {
        return inputKey;
    }

    public InputExpectation setInputKey(String inputKey) {
        this.inputKey = inputKey;
        return this;
    }

    public void getValue(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Interaction.UserInputType.STRING;
    }

    public void getValueInt(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Interaction.UserInputType.INT;
    }

    public InputExpectation getValueDate(String commandName, String key) {
        setInputCommandName(commandName);
        setInputKey(key);
        this.userInputType = Interaction.UserInputType.DATE;
        return this;
    }

    public Map<String, Map<String, String>> getExpectedInput() {
        return expectedInput;
    }

    public InputExpectation setValue(Map<String, Map<String, String>> expectedInput) {
        this.expectedInput = expectedInput;
        return this;
    }

    public void clearExpectedInput(String commandName) {
        inputKey = inputCommandName = null;
        userInputType = null;
        expectedInput.get(commandName).clear();
    }
}