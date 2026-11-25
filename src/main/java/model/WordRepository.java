package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordRepository {
    //быстрая проверка наличия слова
    private final Set<String> allWords = new HashSet<>();
    // получение случайного слова
    private final List<String> wordsList = new ArrayList<>();

    public WordRepository(InputStream inputStream) throws IOException {
        loadWords(inputStream);
    }
    //Читает слова из потока, нормализует их и добавляет в Set (для поиска) и List (для доступа по индексу)
    private void loadWords(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String word = normalize(line.trim());
                    allWords.add(word);
                    wordsList.add(word);
                }
            }
        }
    }

    public void addWord(String word) {
        String normalized = normalize(word);
        if (allWords.add(normalized)) {
            wordsList.add(normalized);
        }
    }

    public boolean contains(String word) {
        return allWords.contains(normalize(word));
    }

    public String getRandomWord() {
        if (wordsList.isEmpty()) return null;
        int index = (int) (Math.random() * wordsList.size());
        return wordsList.get(index);
    }

    private String normalize(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public int size() {
        return wordsList.size();
    }

    public List<String> getAllWords() {
        return wordsList;
    }
}