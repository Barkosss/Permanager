package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;

public class BanCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }

        // ...

        output.output(interaction.setMessage("The user has been banned"));
    }
}
