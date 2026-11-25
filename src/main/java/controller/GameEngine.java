package controller;

import model.GameException;
import model.WordRepository;
import java.util.HashSet;
import java.util.Set;

public class GameEngine {
    private WordRepository repository;
    private final Set<String> usedWords = new HashSet<>();
    private String lastLetter = "";
    private int playerScore = 0;
    private int computerScore = 0;

    public void setRepository(WordRepository repository) {
        this.repository = repository;
    }

    public void startNewGame() {
        usedWords.clear();
        lastLetter = "";
        playerScore = 0;
        computerScore = 0;
    }

    public void playerMove(String word) throws GameException {
        if (repository == null) throw new GameException("Словарь не загружен!");
        String normalizedWord = normalize(word.trim());

        if (normalizedWord.isEmpty()) throw new GameException("Слово не может быть пустым.");
        if (!lastLetter.isEmpty() && !normalizedWord.startsWith(lastLetter)) {
            throw new GameException("Слово должно начинаться на букву: " + lastLetter);
        }
        if (usedWords.contains(normalizedWord)) throw new GameException("Это слово уже было использовано!");
        if (!repository.contains(normalizedWord)) repository.addWord(normalizedWord);

        usedWords.add(normalizedWord);
        playerScore++;
        updateLastLetter(normalizedWord);
    }

    public String computerMove() throws GameException {
        if (repository == null) throw new GameException("Словарь не загружен!");
        String computerWord = null;

         for(int i = 0; i < 100; i++) {
            String candidate = repository.getRandomWord();
            if (candidate != null && !usedWords.contains(candidate)
                    && (lastLetter.isEmpty() || candidate.startsWith(lastLetter))) {
                computerWord = candidate;
                break;
            }
        }

        if (computerWord == null) throw new GameException("Компьютер не смог найти слово. Вы победили!");

        usedWords.add(computerWord);
        computerScore++;
        updateLastLetter(computerWord);
        return computerWord;
    }

    private void updateLastLetter(String word) {
        char lastChar = word.charAt(word.length() - 1);
        if (lastChar == 'ь' || lastChar == 'ы' || lastChar == 'ъ') {
            if (word.length() > 1) lastChar = word.charAt(word.length() - 2);
        }
        lastLetter = String.valueOf(lastChar).toUpperCase();
    }

    public String getLastLetter() { return lastLetter; }
    public int getPlayerScore() { return playerScore; }
    public int getComputerScore() { return computerScore; }

    private String normalize(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
    //Проверка на доступные слова
    public boolean hasAvailableWords() {
        if (repository == null) return false;

        //Смотрим есть ли сслова
        for (String word : repository.getAllWords()) {
            if (!usedWords.contains(word) &&
                    (lastLetter.isEmpty() || word.startsWith(lastLetter))) {
                return true;
            }
        }
        return false;
    }
}