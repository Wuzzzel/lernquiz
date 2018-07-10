package main.java.lernquiz.utils;

import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.IndividualQuestion;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.QuizItem;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDataUtils {

    public static UserData addUserDataToObject(UserData userData, int questionDifficultyInt, boolean questionCorrect, QuizItem quizItem){
        if(userData.getQuestions().get(quizItem.getId()) == null){ //Wenn zu der aktuellen Frage noch keine Einträge vorhanden sind, lege selber Objekte dafür an
            IndividualQuestion individualQuestion = setIndividualQuestionData(questionDifficultyInt, questionCorrect);
            userData.getQuestions().put(quizItem.getId(), individualQuestion);
        }
        //Wenn schon Daten in der Datenbank vorhanden sind, erweitere diese
        userData.getQuestions().get(quizItem.getId()).setLastQuestionDifficulty(questionDifficultyInt);
        userData.getQuestions().get(quizItem.getId()).getEntrys().put(String.valueOf(Instant.now().getEpochSecond()).toString(), new Entry(questionDifficultyInt, questionCorrect)); //Ich glaube der nimmt die Uhrzeit aus Irland
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
        HashMap<String, Entry> entrys = new HashMap<>();
        Entry entry = new Entry(questionDifficultyInt, questionCorrect);
        entrys.put(String.valueOf(Instant.now().getEpochSecond()), entry);
        individualQuestion.setEntrys(entrys);
        return individualQuestion;
    }

    //Geb die schwierigkeit 0-2 (eg. DIFFICULTY_INTEGER_EASY) an und bekomm die passenden Frage IDs aus den userData zurück + Ohne die Frage mit der lastQuestionID
    public static List<String> getCorrespondingQuestionIDs(UserData userData, int difficulty, String lastQuestionID){
        return userData.getQuestions().entrySet().stream().filter(item -> item.getValue().getLastQuestionDifficulty() == difficulty)
                .map(item -> item.getKey()).filter(item -> !item.equals(lastQuestionID)).collect(Collectors.toList());
    }
}
