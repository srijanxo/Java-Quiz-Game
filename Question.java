import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private int points;
    private int negativePoints;
    private String category;
    private Difficulty difficulty;

    public Question(String questionText, List<String> options, String correctAnswer,
                    int points, int negativePoints, String category, Difficulty difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.points = points;
        this.negativePoints = negativePoints;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isCorrect(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getPoints() {
        return points;
    }

    public int getNegativePoints() {
        return negativePoints;
    }

    public String getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    // Convert CSV line into a Question object
    public static Question fromCsv(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6) return null;

        String questionText = parts[0];
        List<String> options = Arrays.asList(parts[1], parts[2], parts[3], parts[4]);
        String correctAnswer = parts[5];

        int points = (parts.length > 6 && !parts[6].isEmpty()) ? Integer.parseInt(parts[6]) : 1;
        int negativePoints = (parts.length > 7 && !parts[7].isEmpty()) ? Integer.parseInt(parts[7]) : 0;
        String category = (parts.length > 8 && !parts[8].isEmpty()) ? parts[8] : "General";

        Difficulty difficulty = Difficulty.EASY;
        if (parts.length > 9) {
            try {
                difficulty = Difficulty.valueOf(parts[9].toUpperCase());
            } catch (IllegalArgumentException e) {
                difficulty = Difficulty.EASY; // default if invalid
            }
        }

        return new Question(questionText, options, correctAnswer, points, negativePoints, category, difficulty);
    }

    // Load from CSV file
    public static List<Question> loadFromCSV(String filename) throws IOException {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                Question q = fromCsv(line);
                if (q != null) questions.add(q);
            }
        }
        return questions;
    }
}
