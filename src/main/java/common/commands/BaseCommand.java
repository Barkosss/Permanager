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

// TODO: Сделать абстрактный класс для команд
// TODO: Сделать метод handlerCommand в котором будет вызываться parseArgs и затем уже run (Убрать вызов parseArgs каждый раз в run)
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
     * @param interaction Object Interaction
     * @return String
     */
    String getCommandDescription(Interaction interaction);

    void commandHandler(Interaction interaction);

    /**
     * Обработка аргументов, которые пользователь указал в сообщении
     *
     * @param interaction Object Interaction
     * @param user        Object User
     */
    void parseArgs(Interaction interaction, User user);

    /**
     * Запустить команду
     *
     * @param interaction Object Interaction
     */
    void run(Interaction interaction);
}
