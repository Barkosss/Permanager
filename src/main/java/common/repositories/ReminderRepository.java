package common.repositories;

import common.models.Reminder;
import common.utils.LoggerHandler;

import java.util.Map;
import java.util.TreeMap;

public class ReminderRepository {
    LoggerHandler logger = new LoggerHandler();
    public Map<Long, Reminder> reminders;

    public ReminderRepository() {
        this.reminders = new TreeMap<>();
    }

    // Создать напоминание в памяти
    public void create(Reminder reminder) {
        long reminderId = reminder.getId();
        if (reminders.containsKey(reminderId)) {
            return;
        }
        reminders.put(reminderId, reminder);
    }

    // Найти напоминание по ID у пользователя
    public Reminder findById(long reminderId) {
        Reminder reminder;
        if ((reminder = reminders.get(reminderId)) != null) {
            return reminder;
        }
        logger.error("Reminder by id(" + reminderId + ") is not found");
        return null;
    }

    // Существует ли напоминание у пользователя
    public boolean existsById(long reminderId) {
        return reminders.get(reminderId) != null;
    }
}