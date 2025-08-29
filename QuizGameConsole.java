import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizGameConsole {
    private static final String QUESTIONS_FILE = "Question.csv";
    private static final String SCORES_FILE = "scores.csv";

    private static final Map<Question.Difficulty, Integer> POINTS = Map.of(
            Question.Difficulty.EASY, 10,
            Question.Difficulty.MEDIUM, 20,
            Question.Difficulty.HARD, 30
    );

    private static final Map<Question.Difficulty, Integer> TIME_LIMIT = Map.of(
            Question.Difficulty.EASY, 15,
            Question.Difficulty.MEDIUM, 12,
            Question.Difficulty.HARD, 10
    );

    private static final int NEGATIVE_MARK = 5; // wrong answer penalty

    public static void main(String[] args) {
        List<Question> questions = loadQuestions(QUESTIONS_FILE);
        if (questions.isEmpty()) {
            System.out.println("No questions found! Put them in " + QUESTIONS_FILE);
            return;
        }
        Collections.shuffle(questions);

        Scanner sc = new Scanner(System.in);

        System.out.println(Ansi.BOLD + "🎯 Welcome to the Quiz Game (Console Edition)!" + Ansi.RESET);
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Player";

        Leaderboard lb = new Leaderboard(SCORES_FILE);
        lb.printTop(5);

        System.out.println("Type 'p' during input to pause. Type 'q' during input to quit early.\n");

        int score = 0;
        int qIndex = 1;
        for (Question q : questions) {
            System.out.printf("%sQuestion %d/%d%s  [%s | %ds | %d pts]%n",
                    Ansi.CYAN, qIndex, questions.size(), Ansi.RESET,
                    q.getDifficulty(), TIME_LIMIT.get(q.getDifficulty()), POINTS.get(q.getDifficulty()));

            if (!q.getCategory().isEmpty()) {
                System.out.println("Category: " + q.getCategory());
            }

            System.out.println(Ansi.BOLD + q.getQuestionText() + Ansi.RESET);

            List<String> opts = q.getOptions();
            List<String> shuffled = new ArrayList<>(opts);
            Collections.shuffle(shuffled);

            for (int i = 0; i < shuffled.size(); i++) {
                System.out.println("  " + (i + 1) + ") " + shuffled.get(i));
            }
            System.out.print("Your choice (1-4): ");

            int time = TIME_LIMIT.get(q.getDifficulty());
            String answer = timedReadLine(time);
            if (answer == null) {
                System.out.println(Ansi.YELLOW + "⏰ Time's up!" + Ansi.RESET);
                score -= NEGATIVE_MARK;
                System.out.println("Penalty: -" + NEGATIVE_MARK);
            } else {
                answer = answer.trim();
                if (answer.equalsIgnoreCase("p")) {
                    System.out.println("Paused. Press Enter to resume...");
                    try { System.in.read(); } catch (IOException ignored) {}
                    // re-ask the same question without penalizing
                    qIndex--;
                    continue;
                } else if (answer.equalsIgnoreCase("q")) {
                    System.out.println("Quitting early. Final score: " + score);
                    lb.add(name, score);
                    System.exit(0);
                }
                int choice = -1;
                try {
                    choice = Integer.parseInt(answer);
                } catch (NumberFormatException ignored) {}
                if (choice >= 1 && choice <= 4) {
                    String chosen = shuffled.get(choice - 1);
                    if (chosen.equalsIgnoreCase(q.getCorrectAnswer())) {
                        System.out.println(Ansi.GREEN + "✅ Correct!" + Ansi.RESET);
                        score += POINTS.get(q.getDifficulty());
                    } else {
                        System.out.println(Ansi.RED + "❌ Wrong!" + Ansi.RESET + " Correct: " + q.getCorrectAnswer());
                        score -= NEGATIVE_MARK;
                        System.out.println("Penalty: -" + NEGATIVE_MARK);
                    }
                } else {
                    System.out.println(Ansi.RED + "Invalid input." + Ansi.RESET + " Correct: " + q.getCorrectAnswer());
                    score -= NEGATIVE_MARK;
                    System.out.println("Penalty: -" + NEGATIVE_MARK);
                }
            }
            System.out.println(Ansi.PURPLE + "Current Score: " + score + Ansi.RESET + "\n");
            qIndex++;
        }

        System.out.println(Ansi.BOLD + "🎉 Game Over! Final score: " + score + Ansi.RESET);
        lb.add(name, score);
        System.out.println("Score saved to leaderboard.");
    }

    private static List<Question> loadQuestions(String path) {
        List<Question> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    list.add(Question.fromCsv(line));
                } catch (Exception e) {
                    System.out.println("Skipping bad line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }
        return list;
    }

    // Read a line from System.in with a timeout in seconds. Returns null on timeout.
    private static String timedReadLine(int timeoutSeconds) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        try {
            Future<String> fut = ex.submit(() -> {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                return br.readLine();
            });
            return fut.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            ex.shutdownNow();
        }
    }
}