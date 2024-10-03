package common;

import common.iostream.Input;
import common.iostream.InputTerminal;
import common.iostream.Output;
import common.iostream.OutputTerminal;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandHandler {
    public Map<String, Method> methodHashMap = new HashMap<>();
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
            String[] args = inputTerminal.getString().split(" ");
            String commandName = args[0];

            // Убираем первый элемент - название команды
            args = Arrays.stream(args).skip(1).toArray(String[]::new);

            // Если команда - выключить бота
            if (commandName.equals("exit")) {
                System.out.println("Program is stop");
                break;
            }

            if (methodHashMap.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                try {
                    methodHashMap.get(commandName).invoke(args);
                } catch(InvocationTargetException | IllegalAccessException err) {
                    System.out.println("[ERROR] Something error: " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("Error: Command is not found. ", true);
            }
        }
    }


    // Загрузка классы команд в хэшмап
    public void commandLoader() {

        String className, packageClass, commandName;
        for(File file : Objects.requireNonNull(new File("./src/main/resources/commandsList.json/").listFiles())) {
            // Если файл не с расширением .java
            if (!file.getName().endsWith(".java")) continue;

            // Если директория - вызываем рекурсию для прохода по директории
            if (file.isDirectory()) {
                listJavaFiles(file);
            }

            // Список файлов
            List<String> folders = Arrays.stream(file.getPath().split("/")).toList();
            // Получаем название класса
            className = Arrays.stream(folders.getLast().split("\\.")).toList().getFirst();
            // Название команды по классу
            commandName = (String)commandObject.get(className);

            // Если команды нет в JSON файле - не добавляем в хэшмап
            if (commandName.isEmpty()) {
                continue;
            }

            // В каком пакете находится команда + класс команды
            packageClass = folders.get(folders.size() - 2) + "." + Arrays.stream(folders.getLast().split("\\.")).toList().getFirst();
            try {
                // Добавляем класс в хэшмап, ключ - название команды
                System.out.println("Command: " + commandName);
                methodHashMap.put(commandName, Class.forName("common." + packageClass).getMethod(commandName));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Рекурсивно проходимся по подпапкам
    private void listJavaFiles(File folder) {
        File[] files = folder.listFiles();

        String className, packageClass, commandName;
        for(File file : Objects.requireNonNull(files)) {
            // Если файл не с расширением .java
            if (!file.getName().endsWith(".java")) continue;

            // Если директория - вызываем рекурсию для прохода по директории
            if (file.isDirectory()) {
                listJavaFiles(file);
            }

            // Список файлов
            List<String> folders = Arrays.stream(file.getPath().split("/")).toList();
            // Получаем название класса
            className = Arrays.stream(folders.getLast().split("\\.")).toList().getFirst();
            // Название команды по классу
            commandName = (String)commandObject.get(className);

            // Если команды нет в JSON файле - не добавляем в хэшмап
            if (commandName.isEmpty()) {
                continue;
            }

            // В каком пакете находится команда + класс команды
            packageClass = folders.get(folders.size() - 2) + "." + Arrays.stream(folders.getLast().split("\\.")).toList().getFirst();
            try {
                // Добавляем класс в хэшмап, ключ - название команды
                methodHashMap.put(commandName, Class.forName("common." + packageClass).getMethod("main"));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}