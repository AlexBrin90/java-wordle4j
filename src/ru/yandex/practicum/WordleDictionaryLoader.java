package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    static final String USER_DIC = System.getProperty("user.dir");
    private final Logger logger;

    public WordleDictionaryLoader(Logger logger) {
        this.logger = logger;
    }

    WordleDictionary loadAllWords(String fileName) throws IOException {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(
                Paths.get(USER_DIC).resolve(fileName).toFile(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 5 && !words.contains(line)) {
                    words.add(line.toLowerCase().replace("ё", "е"));
                }
            }

        } catch (IOException e) {
            logger.error("Ошибка загрузки словаря: " + fileName, e);
            throw e;
        }

        logger.info("Словарь загружен: " + words.size() + " слов");

        return new WordleDictionary(words, logger);
    }
}