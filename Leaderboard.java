import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Leaderboard {
    private final File file;

    public Leaderboard(String path) {
        this.file = new File(path);
    }

    public List<ScoreEntry> readAll() {
        List<ScoreEntry> list = new ArrayList<>();
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    list.add(ScoreEntry.fromCsv(line));
                } catch (Exception ignore) {}
            }
        } catch (IOException e) {
            System.out.println("Could not read scores: " + e.getMessage());
        }
        Collections.sort(list);
        return list;
    }

    public void add(String name, int score) {
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(new ScoreEntry(name, score, LocalDateTime.now()).toCsv());
            fw.write("\n");
        } catch (IOException e) {
            System.out.println("Could not save score: " + e.getMessage());
        }
    }

    public void printTop(int n) {
        List<ScoreEntry> all = readAll();
        System.out.println("\n=== Leaderboard (Top " + n + ") ===");
        for (int i = 0; i < Math.min(n, all.size()); i++) {
            ScoreEntry s = all.get(i);
            System.out.printf("%2d) %-20s %4d  (%s)%n", i + 1, s.getName(), s.getScore(), s.getTimestamp());
        }
        if (all.isEmpty()) System.out.println("No scores yet. Be the first!");
        System.out.println("===============================\n");
    }
}