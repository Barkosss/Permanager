package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
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
    OutputHandler output = new OutputHandler();

    public void read(Interaction interaction, CommandHandler commandHandler) {
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Обработка всех изменений
        interactionTelegram.telegramBot.setUpdatesListener(updates -> {
            List<Content> contents = new ArrayList<>();

            Interaction.Language language;
            for (Update update : updates) {
                ChatMemberUpdated chatMember = update.myChatMember();

                // Проверка, добавили ли бота в беседу
                if (chatMember != null && chatMember.viaJoinRequest()) {
                    long chatId = chatMember.chat().id();
                    output.output(interactionTelegram.setChatId(chatId)
                            .setMessage("Вы добавили меня в чат: " + chatId));
                    continue;
                }

                // Проверка на содержимое сообщения
                if (update.message() == null || update.message().text() == null || update.message().chat() == null) {
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
                logger.debug(String.format("Add new content: %s", contents.getLast()));
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