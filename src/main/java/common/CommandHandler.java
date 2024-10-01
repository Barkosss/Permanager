package common;

import common.iostream.Output;
import common.iostream.OutputTerminal;
import common.iostream.Input;
import common.iostream.InputTerminal;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class CommandHandler {
    public static HashMap<String, Method> methodHashMap = new HashMap<>();
    public static JSONObject commandObject;

    static {
        try {
            commandObject = (JSONObject)new JSONParser().parse(new FileReader("/media/barkos/Data/Project/Permanager/src/main/java/common/commands/commandsList.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Input inputTerminal = new InputTerminal();
    public static Output outputTerminal = new OutputTerminal();

    public void main() {
        // Наполнение хэша командами
        // TODO: Читать название файлов и получать название команды из commandsList.json, чтобы использовать это как ключ

        File rootFolder = new File("/src/main/java/common/commands"); // Замените на вашу корневую папку
        File[] files = rootFolder.listFiles();

        String className;
        for (File file : files) {
            if (file.isDirectory()) {
                // Рекурсивно проходим по всем подпапкам
                listJavaFiles(file);
            } else if (file.getName().endsWith(".java")) {
                // Получаем класс .java
                className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
                try {
                    System.out.println(className);
                    System.out.println((String)commandObject.get(className));
                    methodHashMap.put((String)commandObject.get(className), Class.forName(className).getMethod("main"));
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    throw  new RuntimeException(e);
                }
            }
        }
    }

    private static void listJavaFiles(File folder) {
        File[] files = folder.listFiles();

        String className;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                // Получаем класс .java
                className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
                try {
                    System.out.println(className);
                    System.out.println((String)commandObject.get(className));
                    methodHashMap.put((String)commandObject.get(className), Class.forName(className).getMethod("main"));
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (file.isDirectory()) {
                listJavaFiles(file);
            }
        }
    }

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

            if (methodHashMap.containsKey(commandName)) {

                // Запустить класс, в котором будет работать команда
                methodHashMap.get(commandName);

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("Error: Command is not found. ", true);
            }
        }
    }
}
