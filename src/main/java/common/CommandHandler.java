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
import java.util.*;

/**
 * Класс для обработки команд
 * Метод "commandLoader" загружает команды в хэшмап, для дальнейшего использования.
 * В хэшмапе хранится ключ: название команды, значение: класс команды
 */
public class CommandHandler {
    public Map<String, Class<? extends BaseCommand>> classHashMap = new HashMap<>();
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
    /**
     * Метод для запуска команд.
     * Пользователь вводит команду, если команда найдена в хэшмапе - команда выполняется, иначе - ошибка
     */
    public void getCommand() {

        while(true) {
            outputTerminal.output("Enter command: ", true);
            List<String> args = inputTerminal.getStrings(" ");
            String commandName = args.removeFirst();

            // Если команда "exit" - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                break;
            }

            if (classHashMap.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    classHashMap.get(commandName).getConstructor().newInstance().run();
                } catch(Exception err) {
                    System.out.println("[ERROR] " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("[ERROR] Command \"" + commandName + "\" is not found. ", true);
            }
        }
    }

    /**
     * Загрузчик команд. С помощью Reflections мы получаем все классы из пакета common.commands.
     * В хэшмапе хранится ключ: название команды, значение: класс команды
     */
    public void commandLoader() {
        try {
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            ArrayList<String> arrayPath;
            String className, commandName, packageClass;
            for (Class<? extends BaseCommand> subclass : subclasses) {
                // Массив из путей
                arrayPath = new ArrayList<>(Arrays.asList(subclass.getName().split("\\.")));

                // Название класса
                className = arrayPath.getLast();

                // Название команды по классу
                commandName = (String)commandObject.get(className);

                // Добавляем класс в хэшмап, ключ - название команды
                classHashMap.put(commandName, subclass);
            }

        } catch (Exception err) {
            System.out.println("[ERROR] Command loader: " + err);
        }
    }
}