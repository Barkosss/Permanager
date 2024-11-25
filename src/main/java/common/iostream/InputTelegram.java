package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import common.CommandHandler;
import common.models.Content;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.utils.LoggerHandler;

import java.util.ArrayList;
import java.util.List;

public class InputTelegram {
    LoggerHandler logger = new LoggerHandler();

    public void read(Interaction interaction, CommandHandler commandHandler) {

        // Обработка всех изменений
        ((InteractionTelegram) interaction).telegramBot.setUpdatesListener(updates -> {
            List<Content> contents = new ArrayList<>();

            ((InteractionTelegram) interaction).setChatId(updates.getLast().message().chat().id());

            Interaction.Language language;
            for (Update update : updates) {

                if (update.message() == null || update.message().text() == null || update.message().chat() == null) {
                    continue;
                }

                // Языковой
                language = (update.message().from().languageCode().equals("ru")) ? (Interaction.Language.RUSSIAN)
                        : (Interaction.Language.ENGLISH);
                contents.add(new Content(
                        update.message().from().id(), // Идентификатор пользователя
                        update.message().chat().id(), // Идентификатор чата
                        update.message().text(), // Сообщение пользователя
                        update.message().date(), // Время отправки, пользователем, сообщения
                        language, // Код языка
                        List.of(update.message().text().split(" ")), // Аргументы сообщения
                        Interaction.Platform.TELEGRAM // Платформа, с которой пришёл контент
                ));
            }

            commandHandler.launchCommand(interaction, contents);

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> logger.error("Telegram updates listener: " + err));
    }
}