package common.iostream;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;
import common.CommandHandler;
import common.enums.ModerationCommand;
import common.models.Content;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.ArrayList;
import java.util.List;

public class InputTelegram {
    private final LoggerHandler logger = new LoggerHandler();
    private final OutputHandler output = new OutputHandler();

    // Проверка на добавление бота в чат
    private boolean isJoinChat(InteractionTelegram interactionTelegram, ChatMemberUpdated chatMember) {

        // Проверка на пустые объекты
        if (chatMember.oldChatMember() == null || chatMember.newChatMember() == null) {
            logger.debug("JoinChat check failed: old or new chat member is null.");
            return false;
        }

        // Проверка, что бот на самом деле есть в чате
        // Так как может прилететь старый ивент
        ChatMember botInChat = interactionTelegram.execute(new GetChatMember(chatMember.chat().id(),
                chatMember.oldChatMember().user().id())).chatMember();
        if (botInChat == null) {
            logger.debug("JoinChat check failed: bot is not currently in chat.");
            return false;
        }

        // Старый статус у пользователя это Left?
        if (!chatMember.oldChatMember().status().equals(ChatMember.Status.left)) {
            logger.debug("JoinChat check failed: old status is not LEFT.");
            return false;

            // Новый статус у пользователя это Member?
        } else if (!chatMember.newChatMember().status().equals(ChatMember.Status.member)) {
            logger.debug("JoinChat check failed: new status is not MEMBER.");
            return false;

            // Пользователь бот или нет?
        } else {
            boolean isBotJoin = chatMember.newChatMember().user().isBot()
                    && chatMember.newChatMember().user().username().equals("PermanagerBot");
            logger.debug("JoinChat check passed: bot join status = " + isBotJoin);
            return isBotJoin;
        }
    }

    // Проверка на кик бота из чата
    private boolean isLeaveChat(ChatMemberUpdated chatMember) {

        // Проверка на пустые объекты
        if (chatMember.oldChatMember() == null || chatMember.newChatMember() == null) {
            logger.debug("LeaveChat check failed: old or new chat member is null.");
            return false;
        }

        // Старый статус у пользователя это не Left?
        if (chatMember.oldChatMember().status().equals(ChatMember.Status.left)) {
            logger.debug("LeaveChat check failed: old status already LEFT.");
            return false;

            // Новый статус у пользователя это Left?
        } else if (!chatMember.newChatMember().status().equals(ChatMember.Status.left)) {
            logger.debug("LeaveChat check failed: new status is not LEFT.");
            return false;

            // Пользователь бот или нет?
        } else {
            boolean isBotLeave =  chatMember.oldChatMember().user().isBot()
                    && chatMember.oldChatMember().user().username().equals("PermanagerBot");
            logger.debug("LeaveChat check passed: bot leave status = " + isBotLeave);
            return isBotLeave;
        }
    }

    // Поиск владельца чата
    private ChatMember findChatCreator(InteractionTelegram interactionTelegram, long chatId) {
        // Найти владельца чата
        GetChatAdministratorsResponse administrators = interactionTelegram.execute(new GetChatAdministrators(chatId));
        logger.debug("Looking for chat creator in chat ID: " + chatId);

        for (ChatMember administrator : administrators.administrators()) {
            if (administrator.status().equals(ChatMember.Status.creator)) {
                logger.debug("Chat creator found: @" + administrator.user().username());
                return administrator;
            }
        }

        logger.debug("Chat creator not found for chat ID: " + chatId);
        return null;
    }

    public void read(Interaction interaction, CommandHandler commandHandler) {
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);
        logger.info("Telegram listener has started.");

        // Обработка всех изменений
        interactionTelegram.getTelegramBot().setUpdatesListener(updates -> {
            List<Content> contents = new ArrayList<>();

            Interaction.Language language;
            User user;
            for (Update update : updates) {
                if (update.message() == null) {
                    logger.debug("Received update without message: " + update);
                    continue;
                }

                ChatMemberUpdated chatMember = update.myChatMember();
                long chatId = update.message().chat().id();

                if (update.message().chat().type() != Chat.Type.Private) {
                    // Проверка на администратора канала
                    ChatMember creator = findChatCreator(interactionTelegram, chatId);
                    if (creator != null && !interaction.existsUserById(chatId, creator.user().id())) {
                        interaction.createUser(chatId, creator.user().id())
                                .setPermission(chatId, ModerationCommand.CONFIG, true);
                        logger.info(String.format("New creator registered: @%s in chat %d",
                                creator.user().username(), chatId));
                    } else {
                        interaction.getUser(interaction.getUserId())
                                .setPermission(chatId, ModerationCommand.CONFIG, true);
                        logger.debug("Existing user granted CONFIG permission in chat ID: " + chatId);
                    }
                }


                // Проверка, добавили ли бота в беседу
                if (chatMember != null && isJoinChat(interactionTelegram, chatMember)) {
                    ChatMember creator = findChatCreator(interactionTelegram, chatId);
                    String creatorUsername = (creator == null) ? ("Undefined") : (creator.user().username());

                    output.output(interactionTelegram
                            .setChatId(chatId)
                            .setMessage(String.format(
                                    "Вы добавили меня в чат: %d. Воспользуйтесь командой /start для ознакомления.\n"
                                            + "Создатель: @%s", chatId, creatorUsername
                            )));
                    logger.info("Bot has been added to chat ID: " + chatId);
                    continue;

                    // Проверка на кик бота из чата
                } else if (chatMember != null && isLeaveChat(chatMember)) {
                    logger.info("Bot is leave from chat by id(" + chatMember.chat().id() + ")");
                }

                // Проверка на содержимое сообщения
                if (update.message() == null || update.message().text() == null || update.message().chat() == null) {
                    logger.debug("Skipping update: incomplete message.");
                    continue;
                }

                ((InteractionTelegram) interaction).setChatId(update.message().chat().id())
                        .setUserId(update.message().from().id());

                // Языковой
                language = (update.message().from().languageCode() != null
                        && update.message().from().languageCode().equals("ru")) ? (Interaction.Language.RUSSIAN)
                        : (Interaction.Language.ENGLISH);

                user = interaction.getUser(interaction.getUserId()).setLanguage(language);

                Content content = new Content(
                        update.message().from().username(), // Username пользователя
                        update.message().from().id(), // Идентификатор пользователя
                        update.message().chat(), // Информация о чате
                        update.message().replyToMessage(), // Информация об ответном сообщении
                        update.message().text(), // Содержимое сообщения
                        update.message().date(), // Время отправки, пользователем, сообщения
                        user.getLanguage(), // Язык клиента
                        List.of(update.message().text().split(" ")), // Аргументы сообщения
                        Interaction.Platform.TELEGRAM, // Платформа, с которой пришёл контент
                        update.message(), // Объект сообщения
                        update.message().from(), // Объект пользователя
                        update.chatMember() // Объект участника
                );

                logger.debug("Add new content: " + content);
                contents.add(content);
            }

            logger.info("Dispatching " + contents.size() + " command(s) to CommandHandler.");
            commandHandler.launchCommand(interaction, contents);

            // Вернут идентификатор последнего обработанного обновления или подтверждение их
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

            // Создать обработчик исключений
        }, err -> {
            if (err.response() != null) {
                logger.fatal(String.format("Telegram updates listener (Bad response): %s", err));
            } else {
                logger.fatal(String.format("Telegram updates listener (Network): %s", err), true);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.fatal(String.format("Thread is not sleep: %s", err), true);
                }
            }
        });
    }
}