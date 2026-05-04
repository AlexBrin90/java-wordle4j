package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

public class WordleDictionary {

    private static final Random RNG = new Random();

    List<String> words;
    private final Logger logger;

    public WordleDictionary(List<String> words, Logger logger) {
        this.words = words;
        this.logger = logger;
    }

    public String getRandomWord() {
        if (words == null || words.isEmpty()) {
            if (logger != null) {
                logger.error("Словарь пустой");
            }
            return null;
        }
        return words.get(RNG.nextInt(words.size()));
    }

    public boolean contains(String word) {
        if (word == null || words == null) {
            return false;
        }
        return words.contains(word.toLowerCase().replace("ё", "е"));
    }
}