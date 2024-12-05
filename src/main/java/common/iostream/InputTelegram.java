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

            System.out.println(updates);
            Interaction.Language language;
            for (Update update : updates) {
                System.out.println("-----------------");
                System.out.println("Add new chat: " + update.myChatMember());
                try {
                    System.out.println("Status: " + update.myChatMember().newChatMember().user().username().equals("PermanagerBot"));
                } catch (NullPointerException err) {
                    System.out.println("Status err: " + err);
                }
                System.out.println("-----------------");
                break;
                /* if (update.message() == null || update.message().text() == null || update.message().chat() == null) {
                    continue;
                }

                ((InteractionTelegram) interaction).setChatId(update.message().chat().id())
                        .setUserId(update.message().from().id());

                // Языковой
                language = (update.message().from().languageCode() != null
                        && update.message().from().languageCode().equals("ru")) ? (Interaction.Language.RUSSIAN)
                        : (Interaction.Language.ENGLISH);


                contents.add(new Content(
                        update.message().from().id(), // Идентификатор пользователя
                        update.message().chat(), // Информация о чате
                        update.message().replyToMessage(), // Информация об ответном сообщении
                        update.message().text(), // Содержимое сообщения
                        update.message().date(), // Время отправки, пользователем, сообщения
                        language,
                        List.of(update.message().text().split(" ")), // Аргументы сообщения
                        Interaction.Platform.TELEGRAM // Платформа, с которой пришёл контент
                ));
                logger.debug(String.format("Add new content: %s", contents.getLast()));*/
            }

            commandHandler.launchCommand(interaction, contents);

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> {
            if (err.response() != null) {
                logger.error(String.format("Telegram updates listener (Bad response): %s", err));
            } else {
                logger.error(String.format("Telegram updates listener (Network): %s", err));
            }
        });
    }
}