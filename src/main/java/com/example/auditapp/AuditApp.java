package com.example.auditapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class AuditApp extends Application {

    // Existing fields
    private List<Question> questions;
    private Map<String, List<Question>> chapters;
    private List<String> chapterNames;
    private int currentChapterIndex = 0;
    private int currentQuestionIndex = 0;
    private int totalScore = 0;
    private int totalPossibleScore = 0; // Field to store the total possible score
    private Map<String, Integer> chapterScores = new HashMap<>();

    // Best Practices fields
    private List<BestPractice> bestPractices;
    private Map<String, List<BestPractice>> bestPracticesByChapter;
    private Map<String, Integer> chapterThresholds;

    @Override
    public void start(Stage primaryStage) {
        questions = CSVReaderUtil.readQuestionsFromCSV("src/main/resources/questionnaire/chestionar.csv");
        bestPractices = CSVReaderUtil.readSuggestionFromCSV("src/main/resources/questionnaire/best_practices.csv");

        chapters = questions.stream().collect(Collectors.groupingBy(Question::getChapter));
        chapterNames = new ArrayList<>(chapters.keySet());

        bestPracticesByChapter = bestPractices.stream().collect(Collectors.groupingBy(BestPractice::getChapter));

        // Calculate the total possible score for all questions
        totalPossibleScore = questions.stream()
                .mapToInt(q -> Collections.max(q.getScores()))
                .sum();

        primaryStage.setTitle("Audit App");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ADD8E6;"); // Set background color to light blue

        // Buttons for switching views
        Button quizButton = new Button("Incepe-ți Quiz-ul");
        quizButton.setPrefSize(200, 50);
        quizButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white;"); // Set button color to blue and text color to white

        quizButton.setOnAction(event -> startQuiz(primaryStage));

        root.getChildren().addAll(quizButton);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startQuiz(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER); // Center align the VBox
        root.setStyle("-fx-background-color: #ADD8E6;"); // Set background color to light blue

        Label chapterLabel = new Label();
        chapterLabel.setFont(new Font(20)); // Increase font size
        chapterLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
        Label questionLabel = new Label();
        questionLabel.setFont(new Font(18)); // Increase font size
        questionLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
        ToggleGroup optionsGroup = new ToggleGroup();
        VBox optionsBox = new VBox(5);
        optionsBox.setAlignment(Pos.CENTER); // Center align the options
        Button nextButton = new Button("Următorul");
        nextButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white;"); // Set button color to blue and text color to white

        nextButton.setOnAction(event -> {
            if (areAllQuestionsAnswered(optionsGroup)) {
                updateTotalScore(optionsGroup);
                currentQuestionIndex++;
                List<Question> chapterQuestions = chapters.get(chapterNames.get(currentChapterIndex));
                if (currentQuestionIndex < chapterQuestions.size()) {
                    displayQuestion(chapterQuestions.get(currentQuestionIndex), questionLabel, optionsBox, optionsGroup);
                } else {
                    System.out.println("Capitol " + chapterNames.get(currentChapterIndex) + " completat.");
                    double percentage = calculatePercentage(chapterNames.get(currentChapterIndex));
                    System.out.println("Procentajul obținut: " + percentage + "%");
                    List<BestPractice> bestPractices1 = getSuggestedBestPractices(bestPractices, percentage);
                    for (BestPractice practice : bestPractices1) {
                        System.out.println(practice.toString());
                    }
                    currentQuestionIndex = 0;
                    currentChapterIndex++;
                    if (currentChapterIndex < chapterNames.size()) {
                        displayChapter(chapterLabel, questionLabel, optionsBox, optionsGroup);
                    } else {
                        showResults(primaryStage);
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Raspunde-ți la toate întrebarile!!!");
                alert.showAndWait();
            }
        });

        root.getChildren().addAll(chapterLabel, questionLabel, optionsBox, nextButton);
        displayChapter(chapterLabel, questionLabel, optionsBox, optionsGroup);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
    }

    private void displayChapter(Label chapterLabel, Label questionLabel, VBox optionsBox, ToggleGroup optionsGroup) {
        String currentChapter = chapterNames.get(currentChapterIndex);
        List<Question> chapterQuestions = chapters.get(currentChapter);

        chapterLabel.setText("Capitolul: " + currentChapter);
        displayQuestion(chapterQuestions.get(currentQuestionIndex), questionLabel, optionsBox, optionsGroup);
    }

    private void displayQuestion(Question question, Label questionLabel, VBox optionsBox, ToggleGroup optionsGroup) {
        questionLabel.setText(question.getQuestionText());
        optionsBox.getChildren().clear();
        optionsGroup.getToggles().clear();

        for (int i = 0; i < question.getOptions().size(); i++) {
            RadioButton optionButton = new RadioButton(question.getOptions().get(i));
            optionButton.setUserData(question.getScores().get(i));
            optionButton.setToggleGroup(optionsGroup);
            optionButton.setTextFill(Color.DARKBLUE); // Set option text color to dark blue
            optionsBox.getChildren().add(optionButton);
        }

        // Clear previous answers
        optionsGroup.getToggles().forEach(toggle -> toggle.setSelected(false));
    }

    private boolean areAllQuestionsAnswered(ToggleGroup optionsGroup) {
        return optionsGroup.getSelectedToggle() != null;
    }

    private void updateTotalScore(ToggleGroup optionsGroup) {
        RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
        if (selectedOption != null) {
            int selectedScore = (int) selectedOption.getUserData();
            totalScore += selectedScore;

            String currentChapter = chapterNames.get(currentChapterIndex);
            chapterScores.put(currentChapter, chapterScores.getOrDefault(currentChapter, 0) + selectedScore);
        }
    }

    private void showResults(Stage stage) {
        VBox resultsBox = new VBox(10);
        resultsBox.setAlignment(Pos.CENTER); // Center align the results box
        resultsBox.setStyle("-fx-background-color: #ADD8E6;"); // Set background color to light blue

        Label resultsLabel = new Label("Scorul Total: " + totalScore + " din " + totalPossibleScore); // Show total score from possible score
        resultsLabel.setFont(new Font(20)); // Increase font size
        resultsLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
        resultsBox.getChildren().add(resultsLabel);

        for (String chapter : chapterScores.keySet()) {
            Label chapterScoreLabel = new Label("Capitolul " + chapter + ": " + chapterScores.get(chapter));
            chapterScoreLabel.setFont(new Font(18)); // Increase font size
            chapterScoreLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
            resultsBox.getChildren().add(chapterScoreLabel);
        }

        // Add a button to show assigned best practices
        Button showBestPracticesButton = new Button("Cele mai bune practice");
        showBestPracticesButton.setOnAction(event -> showAssignedBestPractices(stage));
        showBestPracticesButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white;"); // Set button color to blue and text color to white

        resultsBox.getChildren().add(showBestPracticesButton);

        Scene resultsScene = new Scene(resultsBox, 400, 300);
        stage.setScene(resultsScene);
    }

    private void showAssignedBestPractices(Stage stage) {
        VBox assignedBestPracticesBox = new VBox(10);
        assignedBestPracticesBox.setAlignment(Pos.CENTER); // Center align the VBox
        assignedBestPracticesBox.setStyle("-fx-background-color: #ADD8E6;"); // Set background color to light blue
        Label titleLabel = new Label("Cele mai bune practice atribuite");
        titleLabel.setFont(new Font(20)); // Increase font size
        titleLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
        assignedBestPracticesBox.getChildren().add(titleLabel);

        for (String chapter : chapterScores.keySet()) {
            List<BestPractice> chapterBestPractices = bestPracticesByChapter.getOrDefault(chapter, new ArrayList<>());
            double scorePercentage = calculatePercentage(chapter);

            List<BestPractice> suggestedPractices = getSuggestedBestPractices(chapterBestPractices, scorePercentage);
            for (BestPractice bestPractice : suggestedPractices) {
                Label bestPracticeLabel = new Label("Capitolul: " + chapter + " - " + bestPractice.getBestPractice());
                bestPracticeLabel.setFont(new Font(18)); // Increase font size
                bestPracticeLabel.setTextFill(Color.DARKBLUE); // Set text color to dark blue
                assignedBestPracticesBox.getChildren().add(bestPracticeLabel);
            }
        }

        Button backButton = new Button("Înapoi la rezultate");
        backButton.setOnAction(event -> showResults(stage));
        backButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white;"); // Set button color to blue and text color to white
        assignedBestPracticesBox.getChildren().add(backButton);

        Scene bestPracticesScene = new Scene(assignedBestPracticesBox, 600, 400);
        stage.setScene(bestPracticesScene);
    }

    private List<BestPractice> getSuggestedBestPractices(List<BestPractice> bestPractices, double scorePercentage) {
        if (scorePercentage <= 25) {
            return bestPractices.subList(0, Math.min(1, bestPractices.size())); // Suggest first best practice
        } else if (scorePercentage <= 50) {
            return bestPractices.subList(0, Math.min(2, bestPractices.size())); // Suggest first two best practices
        } else if (scorePercentage <= 75) {
            return bestPractices.subList(0, Math.min(3, bestPractices.size())); // Suggest first three best practices
        } else {
            return bestPractices.subList(0, Math.min(4, bestPractices.size())); // Suggest all best practices
        }
    }

    private void showBestPractices(Stage primaryStage) {
        TableView<BestPractice> tableView = new TableView<>();

        TableColumn<BestPractice, String> chapterColumn = new TableColumn<>("Chapter");
        chapterColumn.setCellValueFactory(new PropertyValueFactory<>("chapter"));

        TableColumn<BestPractice, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<BestPractice, String> bestPracticeColumn = new TableColumn<>("Best Practice");
        bestPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("bestPractice"));

        tableView.getColumns().addAll(chapterColumn, categoryColumn, bestPracticeColumn);
        tableView.setItems(FXCollections.observableArrayList(bestPractices));

        Button backButton = new Button("Meniul Principal");
        backButton.setOnAction(event -> start(primaryStage));
        backButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white;"); // Set button color to blue and text color to white

        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #ADD8E6;"); // Set background color to light blue
        root.getChildren().addAll(tableView, backButton);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
    }

    private double calculatePercentage(String chapterName) {
        int score = chapterScores.getOrDefault(chapterName, 0);
        int totalPossibleScoreForChapter = chapters.get(chapterName).stream().mapToInt(q -> Collections.max(q.getScores())).sum();
        return ((double) score / totalPossibleScoreForChapter) * 100;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
