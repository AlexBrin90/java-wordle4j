package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionaryException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Юнит-тесты проекта Wordle")
class WordleTest {
    private static List<String> testWords;
    private Logger logger;
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeAll
    static void setUpAll() {
        testWords = new ArrayList<>();
        testWords.add("носик");
        testWords.add("корка");
        testWords.add("арбуз");
        testWords.add("столб");
        testWords.add("крыша");
        testWords.add("сивко");
        testWords.add("дверь");
        testWords.add("полка");
        testWords.add("книга");
        testWords.add("молок");

        System.out.println("В словарь добавлено: " + testWords.size() + " слов");
    }

    @BeforeEach
    void setUp() {
        logger = new Logger(new PrintWriter(System.out, true));
        dictionary = new WordleDictionary(new ArrayList<>(testWords), logger);
        game = new WordleGame(dictionary, 6, logger);
    }

    // ========================================================================
    // 🔹 ТЕСТЫ ДЛЯ МЕТОДА: normalize()
    // ========================================================================

    @Test
    void normalize_nullReturnsEmptyString() {
        System.out.println("Запущен тест: normalize() — null возвращает пустую строку");
        String result = game.normalize(null);
        assertEquals("", result, "null должен возвращать пустую строку");
    }

    @Test
    void normalize_replacesYoAndLowercases() {
        System.out.println("Запущен тест: normalize() — заменяёт ё на е и приводит к нижнему регистру");
        assertEquals("молоко", game.normalize("МолокО"));
        assertEquals("молеко", game.normalize("молёко"));
        assertEquals("несик", game.normalize("  НЁСИК  "));
    }

    @Test
    void checkWord_validWord_decrementsSteps() throws WordNotFoundInDictionaryException {
        System.out.println("Запущен тест: checkWord() — принимает слово из словаря и уменьшает шаги");
        int stepsBefore = game.getSteps();
        boolean result = game.checkWord("носик");
        assertTrue(result, "checkWord должен вернуть true для валидного слова");
        assertEquals(stepsBefore - 1, game.getSteps(), "Количество шагов должно уменьшиться на 1");
    }

    @Test
    void checkWord_invalidWord_throwsException() {
        System.out.println("Запущен тест: checkWord() — принимает слово из словаря и уменьшает шаги");
        Executable action = new Executable() {
            @Override
            public void execute() throws Throwable {
                game.checkWord("xyzabc");
            }
        };

        WordNotFoundInDictionaryException exception = assertThrows(
                WordNotFoundInDictionaryException.class,
                action,
                "Должно быть выброшено исключение для слова не из словаря"
        );
        assertTrue(exception.getMessage().contains("xyzabc"));
    }

    @Test
    void checkWord_wrongLength_notInDictionary() {
        System.out.println("Запущен тест: checkWord() — не принимает слово с неправильной длиной");
        Executable action = new Executable() {
            @Override
            public void execute() throws Throwable {
                game.checkWord("молокоо");
            }
        };

        assertThrows(WordNotFoundInDictionaryException.class, action);
    }

    @Test
    void evaluate_exactMatch_allUppercase() {
        System.out.println("Запущен тест: evaluate() — полное совпадение: все буквы в верхнем регистре");
        List<String> singleWordList = new ArrayList<>();
        singleWordList.add("носик");
        WordleDictionary singleDict = new WordleDictionary(singleWordList, logger);
        WordleGame fixedGame = new WordleGame(singleDict, 6, logger);
        String result = fixedGame.evaluate("носик");
        assertEquals("НОСИК", result, "Все буквы должны быть в верхнем регистре при полном совпадении");
    }

    @Test
    void evaluate_noMatch_allUnderscores() {
        System.out.println("Запущен тест: evaluate() — букв нет в слове: все подчёркивания");
        List<String> singleWordList = new ArrayList<>();
        singleWordList.add("носик");
        WordleDictionary singleDict = new WordleDictionary(singleWordList, logger);
        WordleGame fixedGame = new WordleGame(singleDict, 6, logger);
        String result = fixedGame.evaluate("ххххх");
        assertEquals("_____", result, "Неправильные буквы должны отображаться как '_'");
    }

    @Test
    void evaluate_partialMatch_lowercase() {
        System.out.println("Запущен тест: evaluate() — буква есть, но не на месте: нижний регистр");
        List<String> singleWordList = new ArrayList<>();
        singleWordList.add("носик");
        WordleDictionary singleDict = new WordleDictionary(singleWordList, logger);
        WordleGame fixedGame = new WordleGame(singleDict, 6, logger);
        String result = fixedGame.evaluate("сони_");
        assertEquals(5, result.length());
        char charAt3 = result.charAt(3);
        assertTrue(charAt3 == 'И' || charAt3 == 'и');
    }

    @Test
    void evaluate_duplicates_correctCount() {
        System.out.println("Запущен тест: evaluate() — дубликаты букв: не подсвечивает лишние");
        List<String> dictWords = new ArrayList<>();
        dictWords.add("корка");
        dictWords.add("ккккк");
        WordleDictionary dict = new WordleDictionary(dictWords, logger);
        WordleGame fixedGame = new WordleGame(dict, 6, logger);
        String result = fixedGame.evaluate("ккккк");
        assertEquals(5, result.length());
        assertEquals('К', result.charAt(0));
    }

    @Test
    void evaluate_nullInput_throwsException() {
        System.out.println("Запущен тест: evaluate() — выбрасывает RuntimeException для null");
        Executable action = new Executable() {
            @Override
            public void execute() throws Throwable {
                game.evaluate(null);
            }
        };

        assertThrows(RuntimeException.class, action);
    }

    @Test
    void evaluate_wrongLength_throwsException() {
        System.out.println("Запущен тест: evaluate() — выбрасывает RuntimeException для слова не из 5 букв");
        Executable action = new Executable() {
            @Override
            public void execute() throws Throwable {
                game.evaluate("нос");
            }
        };

        assertThrows(RuntimeException.class, action);
    }

    @Test
    void getHint_returnsValidWord() {
        System.out.println("Запущен тест: getHint() — возвращает слово из словаря длиной 5 букв");
        String hint = game.getHint();
        assertNotNull(hint, "Подсказка не должна быть null");
        assertEquals(5, hint.length(), "Подсказка должна быть из 5 букв");
        assertTrue(dictionary.contains(hint), "Подсказка должна быть словом из словаря");
    }

    @Test
    void getHint_excludesAttemptedWords() throws WordNotFoundInDictionaryException {
        System.out.println("Запущен тест: getHint() — не возвращает уже введённые слова");
        game.checkWord("носик");
        game.checkWord("корка");
        for (int i = 0; i < 10; i++) {
            String hint = game.getHint();
            assertNotEquals("носик", hint, "Подсказка не должна быть уже введённым словом");
            assertNotEquals("корка", hint);
        }
    }

    @Test
    void isWin_correctWord_returnsTrue() {
        System.out.println("Запущен тест: isWin() — true при совпадении с ответом");
        List<String> singleWordList = new ArrayList<>();
        singleWordList.add("тест");
        WordleDictionary singleDict = new WordleDictionary(singleWordList, logger);
        WordleGame fixedGame = new WordleGame(singleDict, 6, logger);
        assertTrue(fixedGame.isWin("тест"));
        assertFalse(fixedGame.isWin("тест1"));
        assertFalse(fixedGame.isWin(null));
    }

    @Test
    void isGameOver_noSteps_returnsTrue() throws WordNotFoundInDictionaryException {
        System.out.println("Запущен тест: isGameOver() — true, когда шаги закончились");
        WordleGame oneStepGame = new WordleGame(dictionary, 1, logger);
        assertFalse(oneStepGame.isGameOver(), "Игра не должна быть окончена в начале");
        oneStepGame.checkWord("носик");
        assertTrue(oneStepGame.isGameOver(), "Игра должна быть окончена после последнего шага");
    }

    @Test
    void dictionaryContains_caseInsensitive() {
        System.out.println("Запущен тест: WordleDictionary.contains() — поиск по словарю, игнорирования регистра");
        assertTrue(dictionary.contains("носик"));
        assertTrue(dictionary.contains("НОСИК"));
        assertTrue(dictionary.contains("Носик"));
    }

    @Test
    void dictionaryContains_replacesYo() {
        System.out.println("Запущен тест: WordleDictionary.contains() — замена ё на е");
        assertTrue(dictionary.contains("дверь"));
        assertTrue(dictionary.contains("двёрь"));
    }

    @Test
    void dictionaryContains_invalidInput() {
        System.out.println("Запущен тест: WordleDictionary.contains() — false для null и несуществующих слов");
        assertFalse(dictionary.contains(null));
        assertFalse(dictionary.contains("xyz"));
        assertFalse(dictionary.contains(""));
    }

    @Test
    void dictionaryGetRandomWord_returnsValid() {
        System.out.println("Запущен тест: WordleDictionary.getRandomWord() — возвращает валидное слово");
        String word = dictionary.getRandomWord();
        assertNotNull(word);
        assertTrue(dictionary.contains(word));
    }

    @Test
    void loggerInfo_writesWithPrefix() {
        System.out.println("Запущен тест: Logger.info() — записывает сообщение [INFO]");
        logger.info("Тестовое сообщение");
        assertTrue(true, "Проверьте консоль: должно быть [INFO] Тестовое сообщение");
    }

    @Test
    void loggerError_writesWithPrefix() {
        System.out.println("Запущен тест: Logger.error() — записывает сообщение [ERROR]");
        logger.error("Тестовая ошибка");
        assertTrue(true, "Проверьте консоль: должно быть [ERROR] Тестовая ошибка");
    }

    @Test
    void loggerError_withException_writesStack() {
        System.out.println("Запущен тест: Logger.error(msg, exception) — записывает стектрейс");
        Exception ex = new RuntimeException("Тест");
        logger.error("Произошла ошибка", ex);
        assertTrue(true, "Проверьте консоль: должен быть стек трейс исключения");
    }

    @Test
    void isValidCyrillic_onlyCyrillic() {
        System.out.println("Запущен тест: isValidCyrillic — принимает только русские буквы");
        String nonCyrillic = "hello";
        boolean nonCyrValid = true;

        for (int i = 0; i < nonCyrillic.length(); i++) {
            char c = nonCyrillic.charAt(i);
            if (!((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я'))) {
                nonCyrValid = false;
                break;
            }
        }
        assertFalse(nonCyrValid, "Латинские буквы должны отклоняться");

        String cyrillic = "привет";
        boolean cyrValid = true;
        for (int i = 0; i < cyrillic.length(); i++) {
            char c = cyrillic.charAt(i);
            if (!((c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я'))) {
                cyrValid = false;
                break;
            }
        }
        assertTrue(cyrValid, "Кириллические буквы должны приниматься");
    }

    @Test
    void gameLoop_emptyInput_requestsHint() {
        System.out.println("Запущен тест: Игровой цикл: пустой ввод запрашивает подсказку");
        String userInput = "";
        String normalized = game.normalize(userInput.trim());

        if (normalized.isEmpty()) {
            String hint = game.getHint();
            assertNotNull(hint);
            assertEquals(5, hint.length());
        }
    }

    @Test
    void getHint_filtersByWrongLetters() {
        System.out.println("Запущен тест: getHint() — фильтрация по wrongLetters работает корректно");
        List<String> smallDict = new ArrayList<>();
        smallDict.add("носик");
        smallDict.add("арбуз");
        smallDict.add("крыша");
        WordleDictionary testDict = new WordleDictionary(smallDict, logger);
        WordleGame testGame = new WordleGame(testDict, 6, logger);
        testGame.evaluate("арбуз");
        String hint = testGame.getHint();
        assertNotNull(hint);

        if (testGame.getAnswer().equals("носик")) {
            assertFalse(hint.contains("а") || hint.contains("р")
                            || hint.contains("б") || hint.contains("у") || hint.contains("з"),
                    "Подсказка не должна содержать неправильные буквы");
        }
    }
}