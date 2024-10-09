package common.exceptions;

public class BaseException extends Exception {

    // Код исключения
    public int errorCode;
    // Информация об ошибке
    public String message;

    // Конструктор исключение
    public BaseException(String message, int errorCode) {
        this.errorCode = errorCode;
        this.message = message;
    }

    // Получить код ошибки
    public int getErrorCode() {
        return errorCode;
    }

    // Установить код ошибки
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    // Получить сообщение об ошибке
    @Override
    public String getMessage() {
        return message;
    }

    // Установить сообщение об ошибке
    public void setMessage(String message) {
        this.message = message;
    }
}
