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

public class QuizDifficultyHandler implements RequestHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizDifficultyIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.DIFFICULTY_STATE)));
    }

    /**
     * Accepts an input and generates a response
     *
     * @param input request envelope containing request, context and state
     * @return an optional {@link Response} from the handler.
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());

        // Schwierigkeit aus der Antwort auslesen
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String questionDifficulty = getSlotAnswer(intentRequest.getIntent().getSlots(), "difficulty");
        Integer questionDifficultyInt = Constants.DIFFICULTY_INTEGER_MAP.get(questionDifficulty); //Zu einem Integer Mappen, da es mehrere Antwortmöglichkeiten gibt, die aber das selbe bedeuten

        // Frage und boolean, ob die richtige Antwort genannt wurde, aus der Session lesen
        Map<String, String> quizItemMap = (LinkedHashMap<String, String>) sessionAttributes.get(Attributes.QUIZ_ITEM_KEY); // Da man ein JSON zurück bekommt
        QuizItem quizItem = MAPPER.convertValue(quizItemMap, QuizItem.class); // Muss dann mit dem Mapper in das ursprüngliche Objekt gewandelt werden
        boolean questionCorrect = (boolean) sessionAttributes.get(Attributes.QUESTION_CORRECT_KEY);

        //UserData laden und Daten der aktuellen Frage speichern
        UserData userData = loadUserData(input);
        userData = UserDataUtils.addUserDataToObject(userData, questionDifficultyInt, questionCorrect, quizItem);
        DataManager.saveUserData(input, userData);

        //Die aktuelle Frage-ID speichern, wird als check genutzt, damit die gleiche Frage nicht direkt noch mal gewählt wird
        sessionAttributes.put(Attributes.LAST_QUIZ_ITEM_KEY, quizItem.getId());

        //Gespeicherten Quiz Attribute wieder aus der Session löschen, damit JSON-Dokument verkleinert wird
        sessionAttributes.remove(Attributes.QUIZ_ITEM_KEY);
        sessionAttributes.remove(Attributes.QUESTION_CORRECT_KEY);

        String responseText = Constants.QUIZ_ANOTHER_QUESTION_MESSAGE;
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_ANOTHER_QUESTION_REPROMT_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }


    /**
     *
     * @param slots
     * @return
     */
    public static String getSlotAnswer(Map<String, Slot> slots, String slotName) {
        for (Slot slot : slots.values()) {

            // Indication of the results of attempting to resolve the user utterance against the defined slot types
            //Bedeutet: Das Wort das vom Nutzer gesagt wurde, wurde zwar erkannt, aber passt nicht in den definierten slot des Intents
            if(slot.getResolutions() != null && slot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode().equals(StatusCode.ER_SUCCESS_NO_MATCH)) {
                throw new AskSdkException("Antwort des Nutzers passt nicht zu den Utterances des Intents.");
            }
            if(slot.getValue() != null && slot.getName().equals(slotName)){
                return slot.getValue();
            }
        }
        throw new AskSdkException("Keine Daten in Slot vorhanden."); //Exception, da Slots leer sind
    }
}
