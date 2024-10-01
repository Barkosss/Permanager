package common;

import common.commands.*;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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

        String className;
        for(File file : Objects.requireNonNull(new File("/media/barkos/Data/Project/Permanager/src/main/java/common/commands/").listFiles())) {
            if (file.isDirectory() || !file.getName().endsWith(".java")) continue;
            className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
            try {
                methodHashMap.put((String)commandObject.get(className), Class.forName("common.commands." + className).getMethod("main"));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for(File file : Objects.requireNonNull(new File("/media/barkos/Data/Project/Permanager/src/main/java/common/commands/info").listFiles())) {
            if (file.isDirectory() || !file.getName().endsWith(".java")) continue;
            className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
            try {
                methodHashMap.put((String)commandObject.get(className), Class.forName("common.commands.info." + className).getMethod("main"));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for(File file : Objects.requireNonNull(new File("/media/barkos/Data/Project/Permanager/src/main/java/common/commands/moderation").listFiles())) {
            if (file.isDirectory() || !file.getName().endsWith(".java")) continue;
            className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
            try {
                methodHashMap.put((String)commandObject.get(className), Class.forName("common.commands.moderation." + className).getMethod("main"));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
    private static void listJavaFiles(File folder) {
        File[] files = folder.listFiles();

        String className;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                // Получаем класс .java
                className = Arrays.stream(Arrays.stream(file.getPath().split("/")).toList().getLast().split("\\.")).toList().getFirst();
                try {
                    System.out.println(Class.forName("common.commands." + className).getName());
                    methodHashMap.put((String)commandObject.get(className), Class.forName(className).getMethod("main"));
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (file.isDirectory()) {
                System.out.println("Directory: " + file);
                listJavaFiles(file);
            }
        }
    }
    */

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
                try {
                    methodHashMap.get(commandName).invoke(null);
                } catch(InvocationTargetException | IllegalAccessException err) {
                    System.out.println("[ERROR] Something error: " + err);
                }

            } else {
                // Ошибка: Команда не найдена.
                outputTerminal.output("Error: Command is not found. ", true);
            }
        }
    }
}
