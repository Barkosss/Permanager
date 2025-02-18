package common.commands;

import common.models.Interaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReminderCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "reminder";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Управление напоминаниями";
    }

    // Вызвать основной метод команды
    public void run(Interaction interaction) {
        try {
            List<String> arguments = interaction.getArguments();
            // Получаем объект Method, представляющий метод с указанным именем
            String methodName = (arguments.isEmpty()) ? ("help") : (arguments.getFirst());
            Method method = ReminderCommand.class.getMethod(methodName);

            // Создаём экземпляр класса ReminderCommand
            ReminderCommand instance = new ReminderCommand();

            // Вызываем метод на экземпляре класса
            method.invoke(instance);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException err) {
            System.out.println("[ERROR] Reminder command (run): " + err);
        }
    }

    // Метод для вывода справочника команды
    public void help() {
        System.out.println("Reminder command help");
    }

    // Метод для создания напоминания
    public void create() {
        System.out.println("Reminder command create");
    }
}