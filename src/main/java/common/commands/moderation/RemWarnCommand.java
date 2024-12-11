package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.Permissions;
import common.models.User;

public class RemWarnCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "remwarn";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {

        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("..."));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.REMWARN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // ...
        // ...
        // ...

        user.clearExpected(getCommandName());
    }
}
