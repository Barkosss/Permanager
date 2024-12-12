package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;
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

    // Проверка на добавление бота в чат
    private boolean isJoinChat(InteractionTelegram interactionTelegram, ChatMemberUpdated chatMember) {

        // Проверка на пустые объекты
        if (chatMember.oldChatMember() == null || chatMember.newChatMember() == null) {
            return false;
        }

        // Проверка, что бот на самом деле есть в чате
        // Так как может прилететь старый ивент
        ChatMember botInChat = interactionTelegram.telegramBot.execute(new GetChatMember(chatMember.chat().id(),
                chatMember.oldChatMember().user().id())).chatMember();
        if (botInChat == null) {
            return false;
        }

        // Старый статус у пользователя это Left?
        if (!chatMember.oldChatMember().status().equals(ChatMember.Status.left)) {
            return false;

            // Новый статус у пользователя это Member?
        } else if (!chatMember.newChatMember().status().equals(ChatMember.Status.member)) {
            return false;
        } else { // Пользователь бот или нет?
            return chatMember.newChatMember().user().isBot()
                    && chatMember.newChatMember().user().username().equals("PermanagerBot");
        }
    }

    // Проверка на кик бота из чата
    private boolean isLeaveChat(ChatMemberUpdated chatMember) {

        // Проверка на пустые объекты
        if (chatMember.oldChatMember() == null || chatMember.newChatMember() == null) {
            return false;
        }

        // Старый статус у пользователя это не Left?
        if (chatMember.oldChatMember().status().equals(ChatMember.Status.left)) {
            return false;

            // Новый статус у пользователя это Left?
        } else if (!chatMember.newChatMember().status().equals(ChatMember.Status.left)) {
            return false;
        } else { // Пользователь бот или нет?
            return chatMember.oldChatMember().user().isBot()
                    && chatMember.oldChatMember().user().username().equals("PermanagerBot");
        }
    }

    public void read(Interaction interaction, CommandHandler commandHandler) {
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Обработка всех изменений
        interactionTelegram.telegramBot.setUpdatesListener(updates -> {
            List<Content> contents = new ArrayList<>();

            Interaction.Language language;
            for (Update update : updates) {
                ChatMemberUpdated chatMember = update.myChatMember();

                // Проверка, добавили ли бота в беседу
                if (chatMember != null && isJoinChat(interactionTelegram, chatMember)) {

                    long chatId = chatMember.chat().id();
                    ChatMember creator = new ChatMember();

                    // Найти владельца чата
                    GetChatAdministratorsResponse administrators = interactionTelegram.telegramBot
                            .execute(new GetChatAdministrators(chatId));
                    for (ChatMember administrator : administrators.administrators()) {
                        if (administrator.status().equals(ChatMember.Status.creator)) {
                            creator = administrator;
                            break;
                        }
                    }

                    output.output(interactionTelegram
                            .setChatId(chatId)
                            .setMessage(String.format(
                                "Вы добавили меня в чат: %d. Воспользуйтесь командой /start для ознакомления."
                                        + "Создатель: @%s",
                                chatId, creator.user().username()
                            )));


                    continue;
                    // Проверка на кик бота из чата
                } else if (chatMember != null && isLeaveChat(chatMember)) {
                    logger.debug(String.format("Bot is leave from chat by id(%s)", chatMember.chat().id()));
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
                        update.message().from().username(), // Username пользователя
                        update.message().from().id(), // Идентификатор пользователя
                        update.message().chat(), // Информация о чате
                        update.message().replyToMessage(), // Информация об ответном сообщении
                        update.message().text(), // Содержимое сообщения
                        update.message().date(), // Время отправки, пользователем, сообщения
                        Interaction.Language.RUSSIAN, //language,
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