package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;

import java.util.List;

public class ConfigCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    private boolean parseArgs(Interaction interaction) {
        List<String> arguments = interaction.getArguments();
        String argument;
        for (int index = 0; index < arguments.size(); index++) {
            argument = arguments.get(index).toLowerCase();
            switch (index) {
                case 0: { // Проверяем первый аргумент
                    switch (argument) {
                        case "dashboard": {
                            String message = "";
                            output.output(interaction.setMessage(message));
                            return true;
                        }

                        case "user": {
                            // Проверить аргументы на настройку пользователя
                            break;
                        }

                        case "group": {
                            // Проверить аргументы на настройку групп
                            break;
                        }
                    }
                    break;
                }

                case 1: { // Проверяем второй аргумент

                    break;
                }
            }
        }

        return false;
    }

    @Override
    public void run(Interaction interaction) {
        if (parseArgs(interaction)) {
            return;
        }


    }
}
