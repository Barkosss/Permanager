package common.commands;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

import java.util.Map;

public class TestCommand implements BaseCommand {
    public Output output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandDescription() {
        return "Test command for debug";
    }

    @Override
    public void run(Interaction interaction) {
        Map<String, Map<String, String>> expectedInput = interaction.getExpectedInput();

        if (!expectedInput.get(getCommandName()).containsKey("firstMessage")) {
            interaction.getValueInt(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message: ").setInline(true));
            return;
        }

        if (!expectedInput.get(getCommandName()).containsKey("secondMessage")) {
            interaction.getValue(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message: ").setInline(true));
            return;
        }

        String firstMessage = expectedInput.get(getCommandName()).get("firstMessage");
        String secondMessage = expectedInput.get(getCommandName()).get("secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage).setInline(false));
        output.output(interaction.setMessage("Second message: " + secondMessage).setInline(false));
        interaction.clearExpectedInput(getCommandName());
    }
}
