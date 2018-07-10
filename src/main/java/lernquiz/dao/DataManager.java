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
