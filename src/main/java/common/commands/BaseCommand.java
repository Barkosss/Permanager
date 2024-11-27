package common.commands;

import common.models.Interaction;
import common.models.User;

/**
 * Интерфейс команд
 * run() - Основной метод. Используется для запуска команды
 * getCommandName() - Информационный метод. Выводит короткое название команды, которое указывает пользователь
 * parseArgs() - Парсинг аргументов. Аргументы, которые пользователь указал после названия команды
 * getCommandDescription() - Информационный метод. Выводит описание команды. Используется для команды "help"
 */
public interface BaseCommand {

    /**
     * Получить короткое название команды
     *
     * @return String
     */
    String getCommandName();

    /**
     * Получить описание команды
     *
     * @return String
     */
    String getCommandDescription();

    /**
     * Обработка аргументов, которые пользователь указал в сообщении
     *
     * @param interaction Object interaction
     * @param user Object user
     */
    void parseArgs(Interaction interaction, User user);

    /**
     * Запустить команду
     */
    void run(Interaction interaction);
}
