package common.iostream;

import common.models.Content;
import common.models.Interaction;
import common.models.InteractionConsole;
import common.CommandHandler;

import java.util.List;
import java.util.Scanner;


public class InputConsole {
    public Scanner scanner = new Scanner(System.in);
    public Output output = new OutputHandler();

    public String read() {
        return scanner.nextLine();
    }

    public String getString() {
        return read();
    }

    public void listener(Interaction interaction, CommandHandler commandHandler) {
        while(true) {
            // Проверка, ожидаем ли что-то от пользователя
            if (interaction.getUserInputExpectation().getExpectedInputKey() == null) {
                output.output(interaction.setMessage("Enter command: ").setInline(true));
            }

            String userInputMessage = read().trim();


            // Если команда - выключить бота
            if (userInputMessage.equals("exit")) {
                System.out.println("Program is stop");
                System.exit(0);
            }

            commandHandler.launchCommand(interaction, List.of(
                    new Content(userInputMessage, // Сообщение пользователя
                            System.currentTimeMillis() / 1000, // Время отправки, пользователем, сообщения
                            List.of(userInputMessage.split(" ")), // Аргументы сообщения
                            Interaction.Platform.CONSOLE // Платформа, с которой пришёл контент
                    )
                )
            );
        }
    }
}
