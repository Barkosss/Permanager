package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import common.CommandHandler;
import common.models.Content;
import common.models.Interaction;
import common.models.InteractionTelegram;

import java.util.ArrayList;
import java.util.List;

public class InputTelegram {

    public void read(Interaction interaction, CommandHandler commandHandler) {

        // Обработка всех изменений
        ((InteractionTelegram)interaction).TELEGRAM_BOT.setUpdatesListener(updates -> {
            List<Content> contents = new ArrayList<>();

            ((InteractionTelegram)interaction).setUserID(updates.getLast().message().chat().id());

            for(Update update : updates) {
                contents.add(new Content(
                        update.message().text(), // Сообщение пользователя
                        update.message().date(), // Время отправки, пользователем, сообщения
                        List.of(update.message().text().split(" ")) // Аргументы сообщения
                ));
            }

            commandHandler.launchCommand(interaction, contents);

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> System.out.println("[ERROR] Telegram updates listener: " + err));
    }
}