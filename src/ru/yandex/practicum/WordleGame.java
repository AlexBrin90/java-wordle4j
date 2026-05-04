package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotFoundInDictionaryException;

import java.util.*;

public class WordleGame {
    private static final int WORD_LEN = 5;
    private static final Random RNG = new Random();
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final Logger logger;
    private final List<String> attempts = new ArrayList<>();
    private final List<Character> correctLetters = new ArrayList<>();
    private final List<Character> wrongLetters = new ArrayList<>();
    private final Map<Integer, Character> fixedPositions = new HashMap<>();

    public WordleGame(WordleDictionary dict, int steps, Logger logger) {

        this.dictionary = dict;
        this.steps = steps;
        this.logger = logger;
        this.answer = dict.getRandomWord();
        logger.debug("Слово загадано: " + answer.toUpperCase());
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public String normalize(String w) {
        if (w == null) {
            return "";
        }
        return w.toLowerCase().replace("ё", "е").trim();
    }

    public boolean checkWord(String word) throws WordNotFoundInDictionaryException {
        logger.debug("Проверка: " + word.toUpperCase());

        if (!dictionary.contains(word)) {
            logger.info("Слова " +  word.toUpperCase() + " нет в словаре");
            throw new WordNotFoundInDictionaryException(word);
        }

        steps--;
        attempts.add(word);
        logger.debug("Осталось попыток: " + steps);
        return true;
    }

    public String evaluate(String guess) {
        if (guess == null) {
            logger.error("null вместо слова");
            throw new RuntimeException("Слово не может быть пустым");
        }

        if (guess.length() != WORD_LEN) {
            logger.error("Некорректная длина: " + guess.length() + " букв");
            throw new RuntimeException("Ожидалось " + WORD_LEN + " букв");
        }

        String[] res = new String[WORD_LEN];
        Map<Character, Integer> remaining = new HashMap<>();

        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (remaining.containsKey(c)) {
                remaining.put(c, remaining.get(c) + 1);
            } else {
                remaining.put(c, 1);
            }
        }

        for (int i = 0; i < WORD_LEN; i++) {
            char g = guess.charAt(i);
            char a = answer.charAt(i);
            if (g == a) {
                res[i] = String.valueOf(g).toUpperCase();
                remaining.put(g, remaining.get(g) - 1);
                correctLetters.add(g);
                fixedPositions.put(i, g);
            }
        }

        for (int i = 0; i < WORD_LEN; i++) {
            if (res[i] != null) {
                continue;
            }
            char g = guess.charAt(i);
            if (remaining.containsKey(g) && remaining.get(g) > 0) {
                res[i] = String.valueOf(g).toLowerCase();
                remaining.put(g, remaining.get(g) - 1);
                correctLetters.add(g);
            } else {
                res[i] = "_";
                boolean found = false;
                for (int j = 0; j < answer.length(); j++) {
                    if (answer.charAt(j) == g) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    wrongLetters.add(g);
                }
            }
        }

        String result = "";
        for (int i = 0; i < res.length; i++) {
            if (res[i] != null) {
                result = result + res[i];
            } else {
                result = result + "_";
            }
        }

        logger.debug("Результат проверки на соответствие загаданному слову: " + result);
        return result;
    }

    public String getHint() {
        logger.debug("Пользователь запросил подсказку");
        List<String> candidates = new ArrayList<>(dictionary.words);
        List<String> toRemove = new ArrayList<>();
        candidates.removeAll(attempts);

        for (char b : wrongLetters) {
            for (String w : candidates) {
                if (w.contains(String.valueOf(b))) {
                    toRemove.add(w);
                }
            }
        }
        candidates.removeAll(toRemove);

        if (!fixedPositions.isEmpty()) {
            for (int i = candidates.size() - 1; i >= 0; i--) {
                String word = candidates.get(i);
                boolean shouldRemove = false;

                for (Map.Entry<Integer, Character> entry : fixedPositions.entrySet()) {
                    int pos = entry.getKey();
                    char expected = entry.getValue();

                    if (word.charAt(pos) != expected) {
                        shouldRemove = true;
                        break;
                    }
                }

                if (shouldRemove) {
                    candidates.remove(i);
                }
            }
        }

        if (candidates.isEmpty()) {
            logger.error("После отбора словарь пустой. Выводим случайное слово из словаря");
            return dictionary.getRandomWord();
        }

        int index = RNG.nextInt(candidates.size());
        return candidates.get(index);
    }

    public boolean isGameOver() {
        return steps <= 0;
    }

    public boolean isWin(String word) {
        if (word == null) {
            return false;
        }
        return word.equals(answer);
    }
}