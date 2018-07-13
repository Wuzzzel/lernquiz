package main.java.lernquiz.dao;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.PersistenceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.Questions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Map;

public class DataManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Lädt Nutzerdaten aus der DynamoDB, konvertiert sie in ein UserData Objekt und gibt es zurück
     *
     * @param input des aktuellen RequestHandlers
     * @return mit Nutzerdaten gefülltes UserData Objekt {@link UserData}
     */
    public static UserData loadUserData(HandlerInput input) {
        Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
        UserData userData = MAPPER.convertValue(persistentAttributes, UserData.class);
        return userData;
    }

    /**
     * Speichert Nutzerdaten aus dem übergebenen UserData Objekt in die DynamoDB
     *
     * @param input des aktuellen RequestHandlers
     * @param userData zu persistierende Nutzerdaten
     */
    public static void saveUserData(HandlerInput input, UserData userData) {
        Map<String, Object> persistentAttributes = MAPPER.convertValue(userData, Map.class);
        input.getAttributesManager().setPersistentAttributes(persistentAttributes);
        input.getAttributesManager().savePersistentAttributes();
    }

    /**
     * Lädt Quizfragen aus einer definierten XML-Datei, konvertiert sie in ein Questions Objekt und gibt es zurück
     *
     * @return mit Quizfragen gefülltes Questions Objekt {@link Questions}
     * @throws PersistenceException, wenn während der Verarbeitung durch JAXB eine JAXBException geworfen wird
     */
    public static Questions loadQuestions() throws PersistenceException {
        Questions questions;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("questions.xml"); //Angabe der XML-Datei
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Questions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            questions = (Questions) jaxbUnmarshaller.unmarshal(is); //Konvertierung in Questions Objekt
        } catch (JAXBException e) {
            throw new PersistenceException("XML Parser fehler.");
        }
        return questions;
    }
}
