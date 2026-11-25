package test;

import controller.GameEngine;
import model.GameException;
import model.WordRepository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/*модульное тестирование библиотеки классов*/
public class TestProgram {

    public static void main(String[] args) {
        System.out.println("   ТЕСТИРОВАНИЕ БИБЛИОТЕКИ КЛАССОВ\n");
        testWordRepository();
        testGameEngine();
        System.out.println("   ТЕСТИРОВАНИЕ ЗАВЕРШЕНО");
    }

    //WordRepository
    private static void testWordRepository() {
        System.out.println("тесты класса WordRepository");

        String testData = "Москва\nАнастасия\nАнапа\nАстрахань";
        WordRepository repo = null;

        //тест 1:Создание репозиторий, загрузка слов
        try {
            repo = new WordRepository(new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8)));
            if (repo.size() == 4) {
                System.out.println("тест 1 (Загрузка слов из файла): Успех (загружено 4 слова)");
            } else {
                System.out.println("тест 1 (Загрузка слов): Ошибка (неверное количество слов)");
            }
        } catch (Exception e) {
            System.out.println("тест 1: Ошибка - " + e.getMessage());
        }

        //тест 2:Проверка наличия слова
        if (repo != null && repo.contains("Москва")) {
            System.out.println("тест 2 (Проверка наличия слова 'Москва'): Успех");
        } else {
            System.out.println("тест 2 (Проверка наличия слова): Ошибка");
        }

        //тест 3: проверка отсутствия слова
        if (repo != null && !repo.contains("Лондон")) {
            System.out.println("тест 3 (проверка отсутствия слова 'Лондон'): Успех");
        } else {
            System.out.println("тест 3 (проверка отсутствия слова): Ошибка");
        }

        //тест 4:Добавление нового слова
        if (repo != null) {
            repo.addWord("Лондон");
            if (repo.size() == 5 && repo.contains("Лондон")) {
                System.out.println("тест 4 (Добавление нового слова): Успех");
            } else {
                System.out.println("Тест 4 (Добавление нового слова): Ошибка");
            }
        }

        //тест 5:Получение случайного слова
        if (repo != null && repo.getRandomWord() != null) {
            System.out.println("тест 5 (Получение случайного слова): Успех");
        } else {
            System.out.println("тест 5 (Получение случайного слова): Ошибка");
        }

        System.out.println();
    }

    // GameEngine контроллер
    private static void testGameEngine() {
        System.out.println("тесты класса GameEngine");

        // данные
        String testData = "Москва\nАнапа\nАстрахань\nАист\nТигр\nРостов";
        WordRepository repo = null;
        try {
            repo = new WordRepository(new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Ошибка создания репозитория для тестов.");
            return;
        }

        GameEngine engine = new GameEngine();
        engine.setRepository(repo);
        engine.startNewGame();

        // ест 6: Начало игры
        if (engine.getPlayerScore() == 0 && engine.getComputerScore() == 0 && engine.getLastLetter().isEmpty()) {
            System.out.println("тест 6 (Начало новой игры): Успех");
        } else {
            System.out.println("тест 6 (Начало новой игры): Ошибка");
        }

        //тест 7: Корректный ход игрока
        try {
            engine.playerMove("Москва");
            if (engine.getPlayerScore() == 1 && engine.getLastLetter().equals("А")) {
                System.out.println("тест 7 (Корректный ход 'Москва'): Успех (следующая буква 'А')");
            } else {
                System.out.println("тест 7 (Корректный ход): Ошибка (неверная буква или счет)");
            }
        } catch (GameException e) {
            System.out.println("тест 7: Ошибка - " + e.getMessage());
        }

        //тест 8: Ход не на ту букву
        try {
            engine.playerMove("Тигр"); // Должно быть на 'А', а мы вводим 'Т'
            System.out.println("тест 8 (Ход не на ту букву): Ошибка (исключение не выброшено)");
        } catch (GameException e) {
            System.out.println("тест 8 (Ход не на ту букву): Успех (получено исключение: " + e.getMessage() + ")");
        }

        //тест 9:повторное использование слова
        try {
            engine.playerMove("Анапа"); // Вводим слово на 'А'
            engine.playerMove("Анапа"); // Пытаемся ввести его ПОВТОРНО
            System.out.println("Тест 9 (Повтор слова): Ошибка (исключение не выброшено)");
        } catch (GameException e) {
            System.out.println("Тест 9 (Повтор слова): Успех (получено исключение: " + e.getMessage() + ")");
        }

        //тест 10:Проверка наличия доступных слов после ходов
        // (Здесь мы просто проверяем, что метод работает, а не падает с ошибкой)
        if (engine.hasAvailableWords()) {
            System.out.println("Тест 10 (Проверка доступных слов): Успех (слова есть)");
        } else {
            System.out.println("Тест 10 (Проверка доступных слов): Успех (слов нет)");
        }

        //тест 11:Ход компьютера
        try {
            String compWord = engine.computerMove();
            if (compWord != null && !compWord.isEmpty()) {
                System.out.println("Тест 11 (Ход компьютера): Успех (компьютер ответил: " + compWord + ")");
            } else {
                System.out.println("Тест 11 (Ход компьютера): Ошибка (пустой ответ)");
            }
        } catch (GameException e) {
            System.out.println("Тест 11 (Ход компьютера): Ошибка - " + e.getMessage());
        }

        //тест 12:Определение победы игрока (когда у компьютера нет слов)
        String smallData = "Москва";
        try {
            WordRepository smallRepo = new WordRepository(new ByteArrayInputStream(smallData.getBytes(StandardCharsets.UTF_8)));
            GameEngine smallEngine = new GameEngine();
            smallEngine.setRepository(smallRepo);
            smallEngine.startNewGame();
            smallEngine.playerMove("Москва"); // Ход игрока

            if (!smallEngine.hasAvailableWords()) {
                System.out.println("тест 12 (Определение победы игрока): Успех (у компьютера нет слов)");
            } else {
                System.out.println("тест 12 (Определение победы игрока): Ошибка");
            }
        } catch (Exception e) {
            System.out.println("тест 12: Ошибка - " + e.getMessage());
        }

        System.out.println();
    }
}