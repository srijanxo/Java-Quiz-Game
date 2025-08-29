# Improved Quiz Game v2 (Console + GUI)

New additions since v1:
- Negative marking for wrong answers / timeouts (-5 points)
- Optional category column in CSV
- Pause/Resume in both console and GUI
- More tolerant CSV parsing for two common formats

## CSV accepted formats
1) question,opt1,opt2,opt3,opt4,correctAnswer,difficulty
2) question,opt1,opt2,opt3,opt4,correctAnswer,difficulty,category

Example with category:
```
Which planet is known as the Red Planet?,Mercury,Venus,Earth,Mars,Mars,EASY,Space
```

## How to compile & run
```bash
javac *.java
java QuizGameConsole    # console
java QuizGameGUI        # GUI (Swing)
```