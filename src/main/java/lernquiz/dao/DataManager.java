package main.java.lernquiz.dao;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.PersistenceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.IndividualQuestion;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.Questions;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static UserData loadUserData(HandlerInput input){
        Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
        UserData userData = MAPPER.convertValue(persistentAttributes, UserData.class);
        return userData;
    }

    public static void saveUserData(HandlerInput input, UserData userData){
        Map<String, Object> persistentAttributes = MAPPER.convertValue(userData, Map.class);
        input.getAttributesManager().setPersistentAttributes(persistentAttributes);
        input.getAttributesManager().savePersistentAttributes();
    }

    public static UserData addUserDataToObject(UserData userData, int questionDifficultyInt, boolean questionCorrect, QuizItem quizItem){
        if(userData.getQuestions() == null){ //Wenn noch keine Daten in Datenbank vorhanden sind, lege selber Objekte dafür an
            IndividualQuestion individualQuestion = setIndividualQuestionData(questionDifficultyInt, questionCorrect);
            HashMap<String, IndividualQuestion> questions = new HashMap<>();
            questions.put(quizItem.getId(), individualQuestion);
            userData.setQuestions(questions);
        } else if(userData.getQuestions().get(quizItem.getId()) == null){ //Wenn zu der aktuellen Frage noch keine Einträge vorhanden sind, lege selber Objekte dafür an
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


    public static Questions loadQuestions(){
        Questions questions;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("questions.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Questions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            questions = (Questions) jaxbUnmarshaller.unmarshal(is);
        } catch (JAXBException e){
            throw new PersistenceException("XML Parser fehler.");
        }
        return questions;
    }
}
