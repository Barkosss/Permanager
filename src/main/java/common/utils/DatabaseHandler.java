package common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.FileWriter;
import java.io.IOException;

// TODO: Подумать о реализации с Redis
/**
 * Обработчик запросов в Базу Данных
 */
public class DatabaseHandler {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:8080/mydatabase";  // Адрес базы данных
        String user = "barkos";  // Ваше имя пользователя
        String password = "1234";  // Ваш пароль

        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connect accepted");
            // Выполнение SQL-запросов...
        } catch (SQLException err) {
            try (FileWriter writer = new FileWriter("output.txt")) {
                writer.write(err.getMessage()); // Запись строки в файл
                System.out.println("File is writer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Error connection");
        }
    }

    /**
     * Запись значения(-й)
     */
    void write(/* Сигнатура */) {
        // Чтение с базы данных
    }

    /**
     * Чтение значения
     *
     * @return Object
     */
    Object read(/* Сигнатура */) {
        return ""; // Заглушка
    }

    /**
     * Редактировать значение(-я)
     *
     * @return boolean
     */
    boolean edit(/* Сигнатура */) {
        return true; // Заглушка
    }

    /**
     * Проверить, существует ли в БД
     *
     * @return boolean
     */
    boolean check(/* Сигнатура */) {
        return true; // Заглушка
    }
}
