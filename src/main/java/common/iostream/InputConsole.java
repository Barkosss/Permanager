package common.iostream;

import common.CommandHandler;
import common.models.Content;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class InputConsole {
    private final LoggerHandler logger = new LoggerHandler();
    private final Scanner scanner = new Scanner(System.in);
    private final OutputHandler output = new OutputHandler();

    public String read() {
        String input = scanner.nextLine();
        logger.debug(String.format("User input received: \"%s\"", input));
        return input;
    }

    public String getString() {
        return read();
    }

    public void listener(Interaction interaction, CommandHandler commandHandler) {
        logger.info("Console listener has started.");

        try (ScheduledExecutorService schedulerDeleteMessage = Executors.newSingleThreadScheduledExecutor()) {
            schedulerDeleteMessage.schedule(() -> {
            }, 5, TimeUnit.SECONDS);
        } catch (Exception err) {
            logger.error("Failed to schedule message console is listener: " + err.getMessage());
        }

        while (true) {
            try {
                // Проверка, ожидаем ли что-то от пользователя
                if (interaction.getUser(interaction.getUserId()).getInputStatus() != User.InputStatus.WAITING) {
                    output.output(interaction.setMessage("Enter command: ").setInline(true));
                }

                String userInputMessage = read();
                if (userInputMessage.isEmpty()) {
                    continue;
                }
                userInputMessage = userInputMessage.trim().toLowerCase();

                // Если команда - выключить бота
                if (userInputMessage.equals("exit")) {
                    logger.info("Received \"exit\" command. Shutting down the application.");
                    System.out.println("Program is stop");
                    System.exit(0);
                }

                logger.info(String.format("Launching command: %s", userInputMessage));

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
            } catch (Exception err) {
                logger.error("Error while processing console input: " + err.getMessage(), true);
            }
        }
    }
}
