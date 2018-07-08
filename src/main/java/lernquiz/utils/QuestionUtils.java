package main.java.lernquiz.utils;

import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.model.QuizItem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static software.amazon.ion.impl.PrivateIonConstants.False;

public class QuestionUtils {

    static final Logger logger = LogManager.getLogger(QuestionUtils.class);

    /**
     *
     * @param input
     * @return
     */
    public static Optional<Response> generateQuestionResponse(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();

        boolean isFirstQuestion = (boolean) sessionAttributes.get(Attributes.FIRST_QUESTION_KEY);

        String responseText = "";
        // TODO: Datenbank -> Verschiedene Kategorien umsetzten
        if (isFirstQuestion) {
            responseText = Constants.QUIZ_CATEGORY_MESSAGE + " ";
            sessionAttributes.put(Attributes.FIRST_QUESTION_KEY, false);
        }
        // TODO: Fragen aus Datenbank laden und damit das QuizItem beladen

        LinkedHashMap<String, Boolean> testAntworten = new LinkedHashMap<String, Boolean>();
        testAntworten.put("Das ist die erste", Boolean.FALSE);
        testAntworten.put("Das ist die zweite", Boolean.FALSE);
        testAntworten.put("Das ist die dritte", Boolean.TRUE);
        QuizItem quizItem = new QuizItem("Das ist eine Testfrage.", testAntworten);

        // Antworten durchwürfeln, damit diese nicht immmer die selbe reihenfolge haben -> ABER die des Objektes! Damit das auch in den späteren Zuständen so ist

        sessionAttributes.put(Attributes.QUIZ_ITEM_KEY, quizItem);

        responseText += getQuestionText(quizItem);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText); //Wird hier gespeichert, damit es für das Universal Wiederholen bereit gestellt ist

        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_QUESTION_REPROMT_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }


    /**
     *
     * @param quizItem
     * @return
     */
    public static String getQuestionText(QuizItem quizItem) {
        // TODO: Antworten durchnummerieren
        return Constants.QUIZ_QUESTION_MESSAGE + quizItem.getQuestion() + " " + Constants.QUIZ_ANSWER_OPTIONS_MESSAGE + quizItem.getAnswers().entrySet().stream().map(entry -> "Antwort " +
                entry.getKey()).collect(Collectors.joining(". "));
    }

    /**
     *
     * @param input
     * @return
     */
    public static Optional<Response> generateDifficultyResponse(HandlerInput input) { // Dialog 04: Schwierigkeit Abfragen
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        String responseText = (String) sessionAttributes.get(Attributes.RESPONSE_KEY);

        responseText += " " + Constants.QUIZ_DIFFICULTY_MESSAGE;
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);

        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_DIFFICULTY_REPROMT_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }

    /**
     *
     * @param input
     * @param className
     */
    public static void logHandling(HandlerInput input, String className){
        logger.debug(className);
        logger.debug("State_Key: " + input.getAttributesManager().getSessionAttributes().get(Attributes.STATE_KEY));
        if(input.getRequestEnvelope().getRequest().getType().equals("IntentRequest")){ // Denn beim start ist es bspw ein "LaunchRequest" und hat dann kein Intent
            IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
            logger.debug("IntentName: " + intentRequest.getIntent().getName());
            if(intentRequest.getIntent().getSlots() != null) intentRequest.getIntent().getSlots().values().stream().filter(item -> Objects.nonNull(item)).forEach(item -> logger.debug("Slots: " + item)); //Nullcheck, da im Intent keine Slots vorhanden sein können
        }
    }

    public static Optional<Response> generateUniversalOrExceptionResponse(HandlerInput input, TreeMap<String, String> responseText, boolean shouldEndSession){ //String responseText[]
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();

        String currentState = (String) sessionAttributes.get(Attributes.STATE_KEY);
        return Arrays.stream(Attributes.STATES).filter(item -> item.equals(currentState)).findFirst().map(item -> buildResponse(input, responseText.get(item), shouldEndSession)).orElse(buildResponse(input, Constants.GRAMMAR_ERROR_MESSAGE, shouldEndSession));

        /**
        switch ((String) sessionAttributes.get(Attributes.STATE_KEY)){ //Könnte man natürlich auch mit allen States in einem Array->Stream->filtern ob einer zum gesuchten State passt->responseText ist eine Hashmap mit state als key und dem text als value-> damit dann die mehtode zum erzeugen der response aufrufen
            case Attributes.START_STATE: return buildResponse(input, responseText[0], shouldEndSession);
            case Attributes.QUIZ_STATE: return buildResponse(input, responseText[1], shouldEndSession);
            case Attributes.DIFFICULTY_STATE: return buildResponse(input, responseText[2], shouldEndSession);
            case Attributes.ANOTHER_QUESTION_STATE: return buildResponse(input, responseText[3], shouldEndSession);
            case Attributes.STATISTIC_STATE: return buildResponse(input, responseText[4], shouldEndSession);
            default: return buildResponse(input, Constants.GRAMMAR_ERROR_MESSAGE, shouldEndSession);
        }**/
    }

    public static Optional<Response> buildResponse(HandlerInput input, String responseMessage, boolean shouldEndSession){
        return input.getResponseBuilder()
                .withSpeech(responseMessage)
                .withShouldEndSession(shouldEndSession)
                .build();
    }
}
