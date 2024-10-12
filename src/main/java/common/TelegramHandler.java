package common;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class TelegramHandler implements LongPollingUpdateConsumer {

    @Override
    public void consume(List<Update> list) {
        Update update = list.getFirst();
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getText());
        }
    }
}
