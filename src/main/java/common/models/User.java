package common.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class User {

    public enum InputStatus {
        WAITING,
        COMPLETED
    }

    // Идентификатор пользователя
    long userId;

    // Часовой пояс пользователя
    TimeZone timeZone;

    // Язык пользователя
    Interaction.Language language;

    // Объект с информацией ожидаемых ответов
    InputExpectation userInputExpectation = new InputExpectation();

    // Состояние ввода
    InputStatus inputStatus;

    // ChatId -> Модератор
    Map<Long, Member> moderators;

    // Список напоминаний
    Map<Long, Map<Long, Reminder>> reminders;

    // <chatID: <taskID: task>>
    Map<Long, Map<Long, Task>> tasks;

    // Список предупреждений
    Map<Long, Map<Long, Warning>> warnings = new HashMap<>();

    public User(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public User setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public User setLanguage(Interaction.Language language) {
        this.language = language;
        return this;
    }

    public Interaction.Language getLanguage() {
        if (language == null) {
            this.language = Interaction.Language.ENGLISH;
        }
        return language;
    }

    public InputStatus getInputStatus() {
        if (inputStatus == null) {
            this.inputStatus = InputStatus.COMPLETED;
        }
        return inputStatus;
    }

    public InputExpectation.UserInputType getInputType() {
        return Objects.requireNonNullElse(this.userInputExpectation.userInputType,
                InputExpectation.UserInputType.STRING);
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
        if (!this.userInputExpectation.getExpectedInputs().containsKey(commandName)) {
            return null;
        }

        if (!this.userInputExpectation.getExpectedInputs().get(commandName).containsKey(key)) {
            return null;
        }

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

    public void clearExpected(String commandName, String valueKey) {
        this.inputStatus = InputStatus.COMPLETED;
        if (this.userInputExpectation.getExpectedInputs().containsKey(commandName)) {
            this.userInputExpectation.getExpectedInputs().get(commandName).remove(valueKey);
        }
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
        } else {
            long taskId = 0;
            while (this.tasks.get(task.getChatId()).containsKey(taskId)){
                taskId++;
            }
            task.setId(taskId);
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

    public boolean hasPermission(long chatId, Permissions.Permission permission) {
        if (moderators == null) {
            moderators = new HashMap<>();
        }

        Member member = moderators.get(chatId);

        if (member == null) {
            return false;
        }

        return member.getPermissions().canPermission(permission);
    }

    public User setPermission(long chatId, Permissions.Permission permission, boolean permissionStatus) {
        if (moderators == null) {
            moderators = new HashMap<>();
        }

        Member member = moderators.get(chatId);
        if (member == null) {
            member = new Member(userId, -1, true, new Permissions());
            moderators.put(chatId, member);
        }

        member.setPermission(permission, permissionStatus);
        return this;
    }

    public User addWarning(Warning warning) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(warning.getChatId())) {
            this.warnings.put(warning.getChatId(), new HashMap<>());
        }

        this.warnings.get(warning.getChatId()).put(warning.getId(), warning);
        return this;
    }

    public void removeWarning(long chatId, int index) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
        }

        if (!this.warnings.get(chatId).containsKey(Long.parseLong(String.valueOf(index)))) {
            return;
        }

        this.warnings.get(chatId).remove(Long.parseLong(String.valueOf(index)));
    }

    public void resetWarnings(long chatId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            return;
        }

        this.warnings.get(chatId).clear();
    }

    public Map<Long, Warning> getWarnings(long chatId) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }

        if (!this.warnings.containsKey(chatId)) {
            this.warnings.put(chatId, new HashMap<>());
        }

        return this.warnings.get(chatId);
    }
}