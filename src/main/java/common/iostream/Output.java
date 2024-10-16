package common.iostream;

import common.models.Interaction;

/**
 * Интерфейс для вывода
 */
public interface Output {

    /**
     * Вывести значение
     * @param outString Object
     * @param inline Boolean аргумент. true - следующая строка будет новой, иначе false
     */
    void output(Interaction outString, boolean inline);
}