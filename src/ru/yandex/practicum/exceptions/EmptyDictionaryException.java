package ru.yandex.practicum.exceptions;

public class EmptyDictionaryException extends WordleProgramException {
    public EmptyDictionaryException(String message) {
        super(message);
    }
}