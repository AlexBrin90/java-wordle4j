package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

public class Wordle {

    private static final String LOG_FILE = "wordle_game.log";
    private static final String DICT_FILE = "words_ru.txt";
    private static final int MAX_STEPS = 6;
    private static final int WORD_LEN = 5;

    public static void main(String[] args) {

        try (PrintWriter logWriter = new PrintWriter(
                Paths.get(System.getProperty("user.dir"), LOG_FILE).toString(), StandardCharsets.UTF_8)) {

            Logger logger = new Logger(logWriter);
            logger.info("---Запуск игры---");
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dict = loader.loadAllWords(DICT_FILE);
            WordleGame game = new WordleGame(dict, MAX_STEPS, logger);
            logger.info("Слово загадано");

            menu(game, logger);
            logger.info("---Игра окончена---");

        } catch (IOException e) {
            System.err.println("Файловая ошибка: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            System.err.println("Информация в логах: " + LOG_FILE);
            System.exit(1);
        }
    }

    private static void menu(WordleGame game, Logger logger) {
        Scanner scan = new Scanner(System.in);
        printRules();

        try {
            while (game.getSteps() > 0) {
                logger.debug("Попыток осталось: " + game.getSteps());
                System.out.println("Попыток осталось: " + game.getSteps());
                System.out.print("Введите слово: ");

                String input = scan.nextLine();
                String word = game.normalize(input.trim());

                if (word.isEmpty()) {
                    System.out.println("Подсказка: " + game.getHint());
                    logger.debug("Запрошена подсказка");
                    continue;
                }

                if (word.length() != WORD_LEN) {
                    System.out.println("В слове должно быть " + WORD_LEN + " букв");
                    continue;
                }

                if (!isValidCyrillic(word)) {
                    System.out.println("Должны вводится только буквы русского алфавита");
                    logger.debug("Некорректное слово (есть некириллические символы): " + input);
                    continue;
                }

                try {
                    game.checkWord(word);

                    if (game.isWin(word)) {
                        System.out.println("Вы победили! Загаданное слово: " + game.getAnswer().toUpperCase());
                        logger.info("Пользователь угадал слово: " + game.getAnswer());
                        return;
                    }
                    System.out.println(game.evaluate(word));

                } catch (WordNotFoundInDictionaryException e) {
                    System.out.println(e.getMessage());
                    logger.debug("Слово отсутствует в словаре: " + word);
                } catch (WordleGameException e) {
                    System.err.println(e.getMessage());
                    logger.error("Игровая ошибка", e);
                } catch (RuntimeException e) {
                    logger.error("Внутренняя ошибка", e);
                    throw e;
                }
            }

            if (game.isGameOver()) {
                System.out.println("\nПопытки закончились. Правильное слово: " + game.getAnswer().toUpperCase());
                logger.info("Пользователеь проиграл. Правильное слово: " + game.getAnswer());
            }

        } finally {
            scan.close();
        }
    }

    private static void printRules() {
        System.out.println("ПРАВИЛА:");
        System.out.println("Если буква на месте, то она отобразится в ВЕРХНЕМ регистре");
        System.out.println("Если буква присутствует в слове, но не там — она отобразится в нижнем регистре");
        System.out.println("Если буква отсутствует в загаданном слове — '_'");
        System.out.println("Если нужна подсказка - нажмите Enter\n");
        System.out.println("=== ИГРА НАЧАЛАСЬ ===");
    }

    private static boolean isValidCyrillic(String s) {
        for (char c : s.toCharArray()) {
            if (!((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я'))) {
                return false;
            }
        }
        return true;
    }
}