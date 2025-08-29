## Run Console Version
javac *.java
java QuizGameConsole

## Run JavaFX GUI
javac --module-path "C:\javafx-sdk-22\lib" --add-modules javafx.controls,javafx.fxml *.java
java --module-path "C:\javafx-sdk-22\lib" --add-modules javafx.controls,javafx.fxml Main
