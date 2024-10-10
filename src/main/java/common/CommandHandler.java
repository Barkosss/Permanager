package common;

import common.commands.BaseCommand;
import common.iostream.Input;
import common.iostream.InputTerminal;
import common.iostream.Output;
import common.iostream.OutputTerminal;

import org.reflections.Reflections;

import java.util.Set;
import java.util.*;

public class CommandHandler {
    public Map<String, BaseCommand> classHashMap = new HashMap<>();

    public Input inputTerminal = new InputTerminal();
    public Output outputTerminal = new OutputTerminal();


    // Запуск команды
    public void getCommand() {

        while(true) {
            outputTerminal.output("Enter command: ", true);
            List<String> args = inputTerminal.getString(" ");
            String commandName = args.getFirst().toLowerCase();
            args = args.subList(1, args.size());

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                break;
            }

            if (classHashMap.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    classHashMap.get(commandName).run(args);
                } catch(Exception err) {
                    System.out.println("[ERROR] Something error: " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("Error: Command \"" + commandName + "\" is not found. ", true);
            }
        }
    }

    // Загрузчик команд
    public void commandLoader() {
        try {
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            BaseCommand instanceClass;
            for (Class<? extends BaseCommand> subclass : subclasses) {
                instanceClass = subclass.getConstructor().newInstance();

                // Добавляем класс в хэшмап, ключ - название команды, значение - экземпляр класса
                classHashMap.put(instanceClass.getCommandName(), instanceClass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}