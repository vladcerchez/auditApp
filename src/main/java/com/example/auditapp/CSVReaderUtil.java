package com.example.auditapp;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderUtil {

    public static List<Question> readQuestionsFromCSV(String filePath) {
        List<Question> questions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
//                int id = Integer.parseInt(line[0]);
                String chapter = line[0];
                String questionText = line[1];
                List<String> options = List.of(line[2], line[4], line[6], line[8]);
                List<Integer> scores = List.of(
                        Integer.parseInt(line[3]),
                        Integer.parseInt(line[5]),
                        Integer.parseInt(line[7]),
                        Integer.parseInt(line[9])
                );
                questions.add(new Question(chapter, questionText, options, scores));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static List<BestPractice> readSuggestionFromCSV(String filePath) {
        List<BestPractice> bestPractices = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))){
            String[] line;
            reader.skip(1);
            while ((line = reader.readNext()) != null) {
                String chapter = line[0];
                String category = line[1];
                String practice = line[2];

                bestPractices.add(new BestPractice(chapter, category, practice));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return bestPractices;
    }
}