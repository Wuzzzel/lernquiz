package main.java.lernquiz.utils;

import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.IndividualQuestion;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.model.Constants;
import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDataUtils {

    public static UserData addUserDataToObject(UserData userData, int questionDifficultyInt, boolean questionCorrect, QuizItem quizItem){
        if(userData.getQuestions().get(quizItem.getId()) == null){ //Wenn zu der aktuellen Frage noch keine Einträge vorhanden sind, lege selber Objekte dafür an
            IndividualQuestion individualQuestion = setIndividualQuestionData(questionDifficultyInt, questionCorrect);
            userData.getQuestions().put(quizItem.getId(), individualQuestion);
        }
        //Wenn schon Daten in der Datenbank vorhanden sind, erweitere diese
        userData.getQuestions().get(quizItem.getId()).setLastQuestionDifficulty(questionDifficultyInt);
        userData.getQuestions().get(quizItem.getId()).getEntries().put(String.valueOf(Instant.now().toEpochMilli()).toString(), new Entry(questionDifficultyInt, questionCorrect)); //Ich glaube der nimmt die Uhrzeit aus Irland
        return userData;
    }

    /**
     * Erstellt die Objektstruktur für eine IndividualQuestion und einem dazugehörigen Entry
     * @param questionDifficultyInt
     * @param questionCorrect
     * @return
     */
    public static IndividualQuestion setIndividualQuestionData(int questionDifficultyInt, boolean questionCorrect){
        IndividualQuestion individualQuestion = new IndividualQuestion();
        individualQuestion.setLastQuestionDifficulty(questionDifficultyInt);
        HashMap<String, Entry> entries = new HashMap<>();
        Entry entry = new Entry(questionDifficultyInt, questionCorrect);
        entries.put(String.valueOf(Instant.now().toEpochMilli()), entry);
        individualQuestion.setEntries(entries);
        return individualQuestion;
    }

    //Geb die schwierigkeit 0-2 (eg. DIFFICULTY_INTEGER_EASY) an und bekomm die passenden Frage IDs aus den userData zurück + Ohne die Frage mit der lastQuestionID
    public static List<String> getCorrespondingQuestionIDs(UserData userData, int difficulty, String lastQuestionID){
        return userData.getQuestions().entrySet().parallelStream().filter(mapEntry -> mapEntry.getValue().getLastQuestionDifficulty() == difficulty)
                .map(mapEntry -> mapEntry.getKey()).filter(questionId -> !questionId.equals(lastQuestionID)).collect(Collectors.toList());
    }


    // Einträge von einem bestimmten Zeitpunkt bis todate zurück geben
    public static List<Entry> getEntriesToDate(UserData userData, long epochTime){
        return userData.getQuestions().values().parallelStream().flatMap(individualQuestion -> individualQuestion.getEntries().entrySet().stream())
                .filter(mapEntry -> Long.parseLong(mapEntry.getKey()) >= epochTime).map(mapEntry -> mapEntry.getValue()).collect(Collectors.toList());
    }

    public static String getCorrectAnsweredPercent(List<Entry> entries){
        NumberFormat formatter = new DecimalFormat("#0.00");
        double correctAnswered = (double) entries.parallelStream().filter(entry -> entry.isCorrectAnswered() == true).count();
        return formatter.format(correctAnswered / entries.size() * 100).replace(".", ",");
    }

    public static long getAnsweredDifficultyCount(List<Entry> entries, int difficulty){
        return entries.parallelStream().filter(entry -> entry.getQuestionDifficulty() == difficulty).count();
    }
}