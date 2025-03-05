package common.exceptions;

import common.enums.ExceptionsCodes;

public class BaseException extends Exception {

    // Код исключения
    public ExceptionsCodes errorCode;
    // Информация об ошибке
    public String message;

    // Конструктор исключение
    public BaseException(String message, ExceptionsCodes errorCode) {
        this.errorCode = errorCode;
        this.message = message;
    }

    // Получить код ошибки
    public ExceptionsCodes getErrorCode() {
        return errorCode;
    }

    // Установить код ошибки
    public void setErrorCode(ExceptionsCodes errorCode) {
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
