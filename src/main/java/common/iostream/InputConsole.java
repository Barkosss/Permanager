package common.iostream;

import common.CommandHandler;
import common.models.Content;
import common.models.Interaction;
import common.models.User;

import java.util.List;
import java.util.Scanner;


public class InputConsole {
    public Scanner scanner = new Scanner(System.in);
    public OutputHandler output = new OutputHandler();

    public String read() {
        return scanner.nextLine();
    }

    public String getString() {
        return read();
    }

    public void listener(Interaction interaction, CommandHandler commandHandler) {
        while (true) {
            // Проверка, ожидаем ли что-то от пользователя
            if (interaction.getUser(interaction.getUserId()).getInputStatus() != User.InputStatus.WAITING) {
                output.output(interaction.setMessage("Enter command: ").setInline(true));
            }

            String userInputMessage = read().trim();

            // Если команда - выключить бота
            if (userInputMessage.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
            }

            commandHandler.launchCommand(interaction, List.of(
                    new Content(
                            "Console",
                            0L, // Идентификатор пользователя
                            null, // Информация о чате
                            null, // Информация об ответном сообщении
                            userInputMessage, // Содержимое сообщения
                            System.currentTimeMillis() / 1000, // Время отправки, пользователем, сообщения
                            interaction.getUser(interaction.getUserId()).getLanguage(),
                            List.of(userInputMessage.split(" ")), // Аргументы сообщения
                            Interaction.Platform.CONSOLE, // Платформа, с которой пришёл контент
                            null,
                            null,
                            null
                    )
                )
            );
        }
    }
}
