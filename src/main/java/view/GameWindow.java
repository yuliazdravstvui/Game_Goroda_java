package view;

import controller.GameEngine;
import model.GameException;
import model.WordRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameWindow extends JFrame {
    private final GameEngine engine;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JLabel statusLabel;
    private final JComboBox<String> topicBox;

    private final JButton startButton;
    private final JButton submitButton;
    private final JButton giveUpButton;

    //ЦВЕТА
    private final Color bgColor = new Color(245, 245, 250);//ФОН
    private final Color panelColor = new Color(225, 235, 245);//ПаНЕЛИ
    private final Color textColor = new Color(70, 70, 90);//ТЕКСТ
    private final Color startBtnColor = new Color(165, 210, 185);//КНОПКА НАЧАТь
    private final Color submitBtnColor = new Color(175, 205, 235);//КНОПКА ВВОД
    private final Color giveUpBtnColor = new Color(240, 180, 180);//КНОПКА СДАТЬСя

    public GameWindow() {
        this.engine = new GameEngine();

        setTitle("Игра 'Города'");
        setSize(650, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Само окно
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        //МЕНЮ
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(panelColor);
        JMenu aboutMenu = new JMenu("О программе");
        aboutMenu.setForeground(textColor);

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu); // Не забудь добавить в menuBar

        JMenuItem helpItem = new JMenuItem("Справка");
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Правила игры:\n1. Называйте слова.\n2. Слово начинается на последнюю букву.\n" +
                        "3. Если на 'ь', 'ы', 'ъ', берется предпоследняя.\n4. Слова не повторяются.", "Справка", JOptionPane.INFORMATION_MESSAGE));
        JMenuItem devItem = new JMenuItem("О разработчике");
        devItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Разработчик: Корнилова Юлия Константиновна\nГруппа: \tз-425П8-5", "О разработчике", JOptionPane.INFORMATION_MESSAGE));
        aboutMenu.add(helpItem);
        aboutMenu.add(devItem);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);

        //ВЕРХНЯЯ ПАНЕЛЬ (ТЕМА, статус)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBackground(panelColor);
        topPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel topicLabel = new JLabel("Тема:");
        topicLabel.setForeground(textColor);
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(topicLabel);

        topicBox = new JComboBox<>(new String[]{"Города", "Животные", "Растения"});
        topicBox.setFont(new Font("Arial", Font.PLAIN, 14));
        topicBox.setBackground(Color.WHITE);
        topPanel.add(topicBox);

        statusLabel = new JLabel("Нажмите 'Начать' для игры");
        statusLabel.setForeground(textColor);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        //Чат
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(textColor);
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 15, 0, 15),
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1)
        ));
        add(scrollPane, BorderLayout.CENTER);

        //НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(bgColor);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        inputField = new JTextField();
        inputField.setEnabled(false);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setBackground(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(bgColor);

        //кнопки
        startButton = createStyledButton("Начать", startBtnColor);
        submitButton = createStyledButton("Ввод", submitBtnColor);
        giveUpButton = createStyledButton("Сдаться", giveUpBtnColor);

        submitButton.setVisible(false);
        giveUpButton.setVisible(false);

        buttonPanel.add(startButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(giveUpButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        //ОБРАБОТЧИКИ
        startButton.addActionListener(e -> startGame());
        submitButton.addActionListener(e -> handlePlayerMove());
        inputField.addActionListener(e -> handlePlayerMove());
        giveUpButton.addActionListener(e -> giveUp());
    }

    //
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(new Color(60, 60, 60)); // Темный текст для контраста
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private void startGame() {
        String selectedTopic = (String) topicBox.getSelectedItem();
        String fileName = "words_cities.txt";
        if ("Животные".equals(selectedTopic)) fileName = "words_animals.txt";
        else if ("Растения".equals(selectedTopic)) fileName = "words_plants.txt";

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) throw new IOException("Файл " + fileName + " не найден!");
            WordRepository repo = new WordRepository(inputStream);
            engine.setRepository(repo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        engine.startNewGame();
        chatArea.setText("");
        chatArea.append("Тема: " + selectedTopic + "\n\n");
        chatArea.append("ПРАВИЛА:\n");
        chatArea.append("1. Называйте слова по очереди с компьютером.\n");
        chatArea.append("2. Каждое новое слово должно начинаться на последнюю букву предыдущего.\n");
        chatArea.append("3. Если слово заканчивается на ь, ы или ъ, то берется предпоследняя буква.\n");
        chatArea.append("4. Слова не должны повторяться.\n\n");
        chatArea.append("Начнем? Введите слово >_<\n\n");

        statusLabel.setText("Ваш ход. Счет: Вы 0 - Компьютер 0");

        startButton.setVisible(false);
        topicBox.setEnabled(false);
        submitButton.setVisible(true);
        giveUpButton.setVisible(true);
        inputField.setEnabled(true);
        inputField.requestFocus();
    }

    private void handlePlayerMove() {
        String word = inputField.getText();
        if (word.trim().isEmpty()) return;

        try {
            engine.playerMove(word);
            chatArea.append("Вы: " + word + "\n");
            inputField.setText("");

            if (!engine.hasAvailableWords()) {
                finishGame("Вы победили! Компьютер не смог найти слово.");
                return;
            }

            String compWord = engine.computerMove();
            chatArea.append("Компьютер: " + compWord + "\n");

            if (!engine.hasAvailableWords()) {
                finishGame("Вы победили! Больше нет слов, начинающихся на '" + engine.getLastLetter() + "'.");
                return;
            }

            statusLabel.setText("Ваш ход. Буква: " + engine.getLastLetter() +
                    " | Счет: Вы " + engine.getPlayerScore() + " - Компьютер " + engine.getComputerScore());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());

        } catch (GameException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void giveUp() {
        int result = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите сдаться?", "Сдаться", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            finishGame("Вы сдались.");
        }
    }

    private void finishGame(String message) {
        chatArea.append("\n" + message + "\n");
        chatArea.append("Игра окончена. Счет: Вы " + engine.getPlayerScore() + " - Компьютер " + engine.getComputerScore() + "\n");

        statusLabel.setText("Игра окончена. Счет: Вы " + engine.getPlayerScore() + " - Компьютер " + engine.getComputerScore());

        inputField.setEnabled(false);
        submitButton.setVisible(false);
        giveUpButton.setVisible(false);
        startButton.setVisible(true);
        topicBox.setEnabled(true);

        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        //Системный стиль
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}