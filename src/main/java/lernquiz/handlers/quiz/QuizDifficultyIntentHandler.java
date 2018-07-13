package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.IndividualQuestion;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;
import main.java.lernquiz.utils.UserDataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;
import static main.java.lernquiz.dao.DataManager.loadUserData;

public class QuizDifficultyIntentHandler implements RequestHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizDifficultyIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.DIFFICULTY_STATE)));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Verarbeitet die Nutzerangaben bezüglich der Quizfragenschwierigkeit
     * und fragt die Nutzer ob sie eine weitere Quizfrage hören möchten
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        //Schwierigkeit aus der Anfrage auslesen und zu Interger wandeln
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String questionDifficulty = QuestionUtils.getSlotAnswer(intentRequest.getIntent().getSlots(), "difficulty");
        Integer questionDifficultyInt = Constants.DIFFICULTY_INTEGER_MAP.get(questionDifficulty); //Zu einem Integer Mappen, da es mehrere Antwortmöglichkeiten gibt, die aber das selbe bedeuten

        //Quizfrage und boolean, ob die richtige Antwort genannt wurde, aus der Session lesen
        Map<String, String> quizItemMap = (LinkedHashMap<String, String>) sessionAttributes.get(Attributes.QUIZ_ITEM_KEY); //Es wird von JSON zu Map gewandelt
        QuizItem quizItem = MAPPER.convertValue(quizItemMap, QuizItem.class); //Muss dann mit dem Mapper in das ursprüngliche Objekt konvertiert werden
        boolean questionCorrect = (boolean) sessionAttributes.get(Attributes.QUESTION_CORRECT_KEY);

        //Userdaten laden und Daten der aktuellen Frage speichern
        UserData userData = loadUserData(input);
        userData = UserDataUtils.addDataToUserData(userData, questionDifficultyInt, questionCorrect, quizItem);
        DataManager.saveUserData(input, userData);

        //Die aktuelle Frage-ID speichern, wird später als check genutzt, damit die gleiche Frage nicht direkt noch mal gewählt wird
        sessionAttributes.put(Attributes.LAST_QUIZ_ITEM_KEY, quizItem.getId());

        //Gespeicherte Quiz Attribute wieder aus der Session löschen, damit JSON-Dokument verkleinert wird
        sessionAttributes.remove(Attributes.QUIZ_ITEM_KEY);
        sessionAttributes.remove(Attributes.QUESTION_CORRECT_KEY);

        //Antwort setzten, Intentdaten in Session speichern und return
        String responseText = Constants.QUIZ_ANOTHER_QUESTION_MESSAGE;
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_ANOTHER_QUESTION_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }
}
