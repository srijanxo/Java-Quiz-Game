import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizGameGUI extends JFrame {
    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JLabel timerLabel;
    private int timeLeft = 15;
    private javax.swing.Timer timer;  // explicitly use Swing Timer

    public QuizGameGUI(List<Question> questions) {
        this.questions = questions;
        setTitle("Quiz Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        questionLabel = new JLabel("Question");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2));
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            int idx = i;
            optionButtons[i].addActionListener(e -> checkAnswer(optionButtons[idx].getText()));
            optionsPanel.add(optionButtons[i]);
        }
        add(optionsPanel, BorderLayout.CENTER);

        timerLabel = new JLabel("Time left: 15");
        add(timerLabel, BorderLayout.SOUTH);

        loadQuestion();
    }

    private void loadQuestion() {
        if (currentIndex >= questions.size()) {
            endGame();
            return;
        }

        Question q = questions.get(currentIndex);
        questionLabel.setText(q.getQuestionText());

        List<String> opts = q.getOptions();
        List<String> shuffled = new ArrayList<>(opts);
        Collections.shuffle(shuffled);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(shuffled.get(i));
        }

        timeLeft = 15;
        timerLabel.setText("Time left: " + timeLeft);

        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft);
            if (timeLeft <= 0) {
                timer.stop();
                currentIndex++;
                loadQuestion();
            }
        });
        timer.start();
    }

    private void checkAnswer(String answer) {
        Question q = questions.get(currentIndex);
        if (q.isCorrect(answer)) {
            score += q.getPoints();
        } else {
            score -= q.getNegativePoints(); // supports negative marking
        }
        currentIndex++;
        loadQuestion();
    }

    private void endGame() {
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            List<Question> qs = Question.loadFromCSV("Question.csv");
            SwingUtilities.invokeLater(() -> new QuizGameGUI(qs).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
