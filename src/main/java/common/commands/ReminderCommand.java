package common.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReminderCommand implements BaseCommand {

    public String getCommandName() {
        return "reminder";
    }

    public String getCommandDescription() {
        return "Управление напоминаниями";
    }

    public void run(List<String> args) {
        try {
            // Получаем объект Method, представляющий метод с указанным именем
            String methodName = (args.isEmpty()) ? ("help") : (args.getFirst());
            Method method = ReminderCommand.class.getMethod(methodName);

            // Создаём экземпляр класса ReminderCommand
            ReminderCommand instance = new ReminderCommand();

            // Вызываем метод на экземпляре класса
            method.invoke(instance);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException err) {
            System.out.println("[ERROR] Reminder command (run): " + err);
        }
    }

    public void help() {
        System.out.println("Reminder command help");
    }

    public void create() {
        System.out.println("Reminder command create");
    }
}