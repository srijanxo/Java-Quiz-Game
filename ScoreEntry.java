import java.time.*;
import java.time.format.DateTimeFormatter;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private final String name;
    private final int score;
    private final LocalDateTime timestamp;

    public ScoreEntry(String name, int score, LocalDateTime timestamp) {
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String toCsv() {
        return escape(name) + "," + score + "," + timestamp.toString();
    }

    public static ScoreEntry fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 3) throw new IllegalArgumentException("Invalid score line: " + line);
        String name = p[0];
        int score = Integer.parseInt(p[1]);
        LocalDateTime ts = LocalDateTime.parse(p[2]);
        return new ScoreEntry(name, score, ts);
    }

    private static String escape(String s) {
        return s.replace(",", " "); // simple escape
    }

    @Override
    public int compareTo(ScoreEntry o) {
        int c = Integer.compare(o.score, this.score);
        if (c != 0) return c;
        return o.timestamp.compareTo(this.timestamp);
    }
}