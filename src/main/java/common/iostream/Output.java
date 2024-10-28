package common.iostream;

import common.models.InteractionTelegram;

/**
 * Интерфейс для вывода
 */
public interface Output {

    void output(InteractionTelegram interaction);
}