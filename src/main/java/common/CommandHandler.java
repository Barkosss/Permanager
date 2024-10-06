package common;

import common.commands.BaseCommand;
import common.iostream.Input;
import common.iostream.InputTerminal;
import common.iostream.Output;
import common.iostream.OutputTerminal;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.*;

public class CommandHandler {
    public Map<String, BaseCommand> classHashMap = new HashMap<>();
    public JSONObject commandObject;

    {
        try {
            commandObject = ((JSONObject)new JSONParser().parse(new FileReader("./src/main/resources/commandsList.json/")));
        } catch (IOException | ParseException err) {
            throw new RuntimeException(err);
        }
    };

    public Input inputTerminal = new InputTerminal();
    public Output outputTerminal = new OutputTerminal();


    // Запуск команды
    public void getCommand() {

        while(true) {
            outputTerminal.output("Enter command: ", true);
            String commandName = inputTerminal.getString().toLowerCase();

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                break;
            }

            if (classHashMap.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    classHashMap.get(commandName).run();
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

            ArrayList<String> arrayPath;
            String className, commandName;
            for (Class<? extends BaseCommand> subclass : subclasses) {

                // Массив из путей
                arrayPath = new ArrayList<>(Arrays.asList(subclass.getName().split("\\.")));

                // Название класса
                className = arrayPath.getLast();

                // Название команды по классу
                commandName = (String)commandObject.get(className);

                // Добавляем класс в хэшмап, ключ - название команды
                classHashMap.put(commandName, subclass.getConstructor().newInstance());
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}