package common.commands;

import com.pengrad.telegrambot.model.request.*;
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

        // Кнопки доступные всем - Прикреплены к сообщению
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("url").url("www.google.com"),
                new InlineKeyboardButton("callback_data").callbackData("callback_data"),
                new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query"));

        // Кнопки видные только пользователю
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup(
                new KeyboardButton("text"),
                new KeyboardButton("contact").requestContact(true),
                new KeyboardButton("location").requestLocation(true));

        output.output(interaction.setMessage("First message: " + firstMessage).setReplyKeyboard(replyKeyboard).setInline(false));
        interaction.sleep(3000);
        output.output(interaction.setMessage("Second message: " + secondMessage).setInlineKeyboard(inlineKeyboard).replyKeyboardRemove().setInline(false));
        interaction.sleep(10000);
        output.output(interaction.setMessage("You wait 10 seconds"));
        interaction.clearExpectedInput(getCommandName());
    }
}
