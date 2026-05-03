package ru.yandex.practicum.exceptions;

public class WordleProgramException extends Exception {
    public WordleProgramException(String message) {
        super(message);
    }
    public WordleProgramException(String message, Throwable cause) {
        super(message, cause);
    }
}