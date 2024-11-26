package common.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public enum InputStatus {
        WAITING,
        COMPLETED
    }

    // Идентификатор пользователя
    long userId;

    // Объект с информацией ожидаемых ответов
    InputExpectation userInputExpectation = new InputExpectation();

    // Состояние ввода
    InputStatus inputStatus;

    // ...
    List<Member> members = new ArrayList<>();

    // ...
    Map<Server, List<Warning>> warnings = new HashMap<>();

    public User(long userId) {
        this.userId = userId;
    }

    public InputStatus getInputStatus() {
        if (inputStatus == null) {
            this.inputStatus = InputStatus.COMPLETED;
        }
        return inputStatus;
    }

    public void setValue(Object value) {
        InputExpectation inputExpectation = this.userInputExpectation;
        inputExpectation.getExpectedInputs().get(inputExpectation.expectedCommandName)
                .put(inputExpectation.expectedInputKey, value);
    }

    public Object getValue(String commandName, String key) {
        return this.userInputExpectation.getExpectedInputs().get(commandName).get(key);
    }

    public String getCommandException() {
        return this.userInputExpectation.expectedCommandName;
    }

    public boolean isExceptedKey(String commandName, String key) {
        if (this.userInputExpectation.getExpectedInputs().isEmpty()) {
            return false;
        }
        return this.userInputExpectation.getExpectedInputs().get(commandName).containsKey(key);
    }

    public User setExcepted(String commandName, String valueKey) {
        this.inputStatus = InputStatus.WAITING;
        this.userInputExpectation.setExpected(commandName, valueKey);
        return this;
    }

    public void clearExpected(String commandName) {
        this.inputStatus = InputStatus.COMPLETED;
        this.userInputExpectation.getExpectedInputs().remove(commandName);
    }
}