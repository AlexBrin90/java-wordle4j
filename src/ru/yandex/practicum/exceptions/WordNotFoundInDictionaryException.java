package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionaryException extends WordleGameException {
    public WordNotFoundInDictionaryException(String word) {
        super("Слово \"" + word + "\" не найдено в словаре");
    }
}