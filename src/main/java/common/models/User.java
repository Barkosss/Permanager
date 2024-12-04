package common.models;

import common.repositories.WarningRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class User {

    public enum InputStatus {
        WAITING,
        COMPLETED
    }

    public enum Permissions {
        BAN,
        UNBAN,
        MUTE,
        UNMUTE,
        KICK,
        WARN,
        REMWARN,
        RESETWARNS,
        CLEAR,
        CONFIG
    }

    // Идентификатор пользователя
    long userId;

    // Объект с информацией ожидаемых ответов
    InputExpectation userInputExpectation = new InputExpectation();

    // Состояние ввода
    InputStatus inputStatus;

    // ChatId -> Модератор
    Map<Long, Member> moderators;

    // Список напоминаний
    Map<Long, Map<Long, Reminder>> reminders;

    // ...
    Map<Long, Map<Long, Task>> tasks;

    // Список предупреждений
    Map<Server, WarningRepository> warnings = new HashMap<>();

    public User(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public InputStatus getInputStatus() {
        if (inputStatus == null) {
            this.inputStatus = InputStatus.COMPLETED;
        }
        return inputStatus;
    }

    public InputExpectation.UserInputType getInputType() {
        return Objects.requireNonNullElse(this.userInputExpectation.userInputType, InputExpectation.UserInputType.STRING);
    }

    public void setValue(Object value) {
        InputExpectation inputExpectation = this.userInputExpectation;
        if (inputExpectation.expectedCommandName == null) {
            return;
        }
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
        this.userInputExpectation.setExpected(commandName, valueKey, InputExpectation.UserInputType.STRING);
        return this;
    }

    public User setExcepted(String commandName, String valueKey, InputExpectation.UserInputType inputType) {
        this.inputStatus = InputStatus.WAITING;
        this.userInputExpectation.setExpected(commandName, valueKey, inputType);
        return this;
    }

    public void clearExpected(String commandName) {
        this.inputStatus = InputStatus.COMPLETED;
        this.userInputExpectation.getExpectedInputs().remove(commandName);
    }

    public User addReminder(Reminder reminder) {
        if (this.reminders == null) {
            this.reminders = new HashMap<>();
        }

        if (!this.reminders.containsKey(reminder.chatId)) {
            this.reminders.put(reminder.chatId, new HashMap<>());
        }

        this.reminders.get(reminder.chatId).put(reminder.getId(), reminder);
        return this;
    }

    public User removeReminder(Reminder reminder) {
        if (this.reminders == null) {
            this.reminders = new HashMap<>();
        }

        if (!this.reminders.containsKey(reminder.chatId)) {
            this.reminders.put(reminder.chatId, new HashMap<>());
        }

        this.reminders.get(reminder.chatId).remove(reminder.getId());
        return this;
    }

    public Map<Long, Reminder> getReminders(long chatId) {
        if (this.reminders == null) {
            this.reminders = new HashMap<>();
        }

        if (!this.reminders.containsKey(chatId)) {
            this.reminders.put(chatId, new HashMap<>());
        }

        return this.reminders.get(chatId);
    }

    public User addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new HashMap<>();
        }
        if (!this.tasks.containsKey(task.getChatId())) {
            this.tasks.put(task.getChatId(), new HashMap<>());
        }
        this.tasks.get(task.getChatId()).put(task.getId(), task);
        return this;
    }

    public User removeTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new HashMap<>();
        }
        if (!this.tasks.containsKey(task.getChatId())) {
            this.tasks.put(task.getChatId(), new HashMap<>());
        }
        this.tasks.get(task.getChatId()).remove(task.getId());
        return this;
    }

    public Map<Long, Task> getTasks(long chatId) {
        if (this.tasks == null) {
            this.tasks = new HashMap<>();
        }
        if (!this.tasks.containsKey(chatId)) {
            this.tasks.put(chatId, new HashMap<>());
        }
        return this.tasks.get(chatId);
    }

    public boolean hasPermission(long chatId, Permissions permission) {
        Member member = moderators.get(chatId);

        if (member == null) {
            return false;
        }

        return member.permissions.canPermission(permission);
    }
}