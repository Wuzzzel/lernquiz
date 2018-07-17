package main.java.lernquiz.utils;

import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.IndividualQuestion;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.QuizItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class UserDataUtils {

    /**
     * Erweitert das übergebene Attribut userData, um einen Eintrag (Entry) mit Hilfe der weiteren übergebenen Attribute
     *
     * @param userData              Objekt das um einen Eintrag erweitert werden soll
     * @param questionDifficultyInt zu setzender Wert des neuen Eintrags
     * @param questionCorrect       zu setzender Wert des neuen Eintrags
     * @param quizItem              zum neuen Eintrag zugehörige Quizfrage
     * @return erweitertes UserData Objekt {@link UserData}
     */
    public static UserData addDataToUserData(UserData userData, int questionDifficultyInt, boolean questionCorrect, QuizItem quizItem) {
        if (userData.getQuestions().get(quizItem.getId()) == null) { //Wenn zu der Quizfrage (quizItem) noch keine Einträge in userData vorhanden sind, lege selber Objekte dafür an
            IndividualQuestion individualQuestion = setIndividualQuestionData(questionDifficultyInt, questionCorrect);
            userData.getQuestions().put(quizItem.getId(), individualQuestion);
        }
        //Wenn schon Einträge im userData Objekt vorhanden sind, erweitere diese
        userData.getQuestions().get(quizItem.getId()).setLastQuestionDifficulty(questionDifficultyInt);
        userData.getQuestions().get(quizItem.getId()).getEntries().put(String.valueOf(Instant.now().toEpochMilli()).toString(), new Entry(questionDifficultyInt, questionCorrect));
        return userData;
    }

    /**
     * Erstellt die Objektstruktur für eine IndividualQuestion und einem dazugehörigen Eintrag (Entry), mit Hilfe der übergebenen Attribute
     *
     * @param questionDifficultyInt zu setzender Wert des neuen Eintrags
     * @param questionCorrect       zu setzender Wert des neuen Eintrags
     * @return ein IndividualQuestion Objekt, dass einen Eintrag bestehend aus den übergebenen Attributen enthält {@link IndividualQuestion}
     */
    public static IndividualQuestion setIndividualQuestionData(int questionDifficultyInt, boolean questionCorrect) {
        IndividualQuestion individualQuestion = new IndividualQuestion();
        individualQuestion.setLastQuestionDifficulty(questionDifficultyInt);
        HashMap<String, Entry> entries = new HashMap<>();
        Entry entry = new Entry(questionDifficultyInt, questionCorrect);
        entries.put(String.valueOf(Instant.now().toEpochMilli()), entry);
        individualQuestion.setEntries(entries);
        return individualQuestion;
    }

    /**
     * Gibt eine Liste an Quizfragen-IDs (questionId), exklusive der übergebenen Id lastQuestionID,
     * zur angegebenen Quizfragen-Schwierigkeit (difficulty), aus dem userData Objekt zurück
     *
     * @param userData       aus dem die Quizfragen-IDs genommen werden sollen
     * @param difficulty     Wert den die Quizfragen-IDs inne haben sollen
     * @param lastQuestionID Id die nicht in der zurückzugebenen Liste enthalten sein soll
     * @return Liste an Quizfragen-IDs nach den übergebenen Richtwerten {@link List<String>}
     */
    public static List<String> getCorrespondingQuestionIDs(UserData userData, int difficulty, String lastQuestionID) {
        return userData.getQuestions().entrySet().parallelStream().filter(mapEntry -> mapEntry.getValue()
                .getLastQuestionDifficulty() == difficulty).map(mapEntry -> mapEntry.getKey())
                .filter(questionId -> !questionId.equals(lastQuestionID)).collect(Collectors.toList());
    }

    /**
     * Gibt eine Liste an Einträgen (Entry), die nach dem übergebenen Zeitpunkt (epochTime) erstellt wurden, aus den userData zurück
     *
     * @param userData  aus den die Einträge genommen werden sollen
     * @param epochTime Zeitpunkt der angibt, wann die Einträge mindestens erstellt wurden müssen
     * @return Liste an Einträgen nach den übergebenen Richtwerten {@link List<Entry>}
     */
    public static List<Entry> getEntriesToDate(UserData userData, long epochTime) {
        return userData.getQuestions().values().parallelStream()
                .flatMap(individualQuestion -> individualQuestion.getEntries().entrySet().parallelStream())
                .filter(mapEntry -> Long.parseLong(mapEntry.getKey()) >= epochTime)
                .map(mapEntry -> mapEntry.getValue()).collect(Collectors.toList());
    }

    /**
     * Gibt die Anzahl der korrekt beantworteten Quizfragen, aus der übergebenen Entry-Liste, in Prozent zurück
     *
     * @param entries Liste, dessen Einträge verarbeitet werden sollen
     * @return Anzahl der korrekt beantworteten Quizfragen als String in der Form #X,XX {@link String}
     */
    public static String getCorrectAnsweredPercent(List<Entry> entries) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        double correctAnswered = (double) entries.parallelStream().filter(entry -> entry.isCorrectAnswered()).count();
        return formatter.format(correctAnswered / entries.size() * 100).replace(".", ",");
    }

    /**
     * Gibt die Anzahl der zur übergebenen Quizfragen-Schwierigkeit (difficulty) gefundenen Einträge (Entry)
     * aus der Liste entries zurück
     *
     * @param entries    Liste, dessen Einträge verarbeitet werden sollen
     * @param difficulty Quizfragen-Schwierigkeit dessen Einträge gesucht werden
     * @return Anzahl der gefundenen Einträge mit der angegebenen difficulty {@link long}
     */
    public static long getAnsweredDifficultyCount(List<Entry> entries, int difficulty) {
        return entries.parallelStream().filter(entry -> entry.getQuestionDifficulty() == difficulty).count();
    }
}