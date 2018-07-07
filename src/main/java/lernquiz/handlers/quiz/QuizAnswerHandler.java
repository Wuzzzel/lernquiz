package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.exception.PersistenceException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.beans.binding.IntegerBinding;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.model.QuizItem;
import main.java.lernquiz.utils.AnswerOutOfBoundsException;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class QuizAnswerHandler implements RequestHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizAnswerIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.QUIZ_STATE))); //TODO: Ggf. hier nur nach STATE_KEY schauen, da die Buchstaben (letter) Antwort sonst nicht funktioniert. Oder Buchstabenantwort einfach ganz raus nehmen!
    }

    //TODO: Mit Datenbank umsetzen oder doch mit input.getAttributesManager().savePersistentAttributes(); bzw input.getAttributesManager().setPersistentAttributes();

    /**
     * Accepts an input and generates a response
     *
     * @param input request envelope containing request, context and state
     * @return an optional {@link Response} from the handler.
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        String responseText;
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();

        QuestionUtils.logHandling(input, this.getClass().getName());

        Map<String, String> quizItemMap = (LinkedHashMap<String, String>) sessionAttributes.get(Attributes.QUIZ_ITEM_KEY); // Da man ein JSON zurück bekommt
        QuizItem quizItem = MAPPER.convertValue(quizItemMap, QuizItem.class); // Muss dann mit dem Mapper in das ursprüngliche Objekt gewandelt werden

        boolean correct = compareSlots(intentRequest.getIntent().getSlots(), quizItem.getAnswers().size(), getCorrectAnswer(quizItem)); // Man könnte auch den Key der korrekten Antwort an der bestimmten Stelle im Baum nutzen,
        // dann hat man sofort den String der richtigen Antwort. Wird das benötigt?
        // Nicht auffordernder Dialog 05: Antwort bestätigen
        if (correct) {
            responseText = Constants.QUIZ_ANSWER_CORRECT_MESSAGE;
        } else {
            responseText = Constants.QUIZ_ANSWER_WRONG_MESSAGE;
        }

        sessionAttributes.put(Attributes.STATE_KEY, Attributes.DIFFICULTY_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);

        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return QuestionUtils.generateDifficultyResponse(input);
    }


    /**
     * @param slots
     * @param amountOfAnswers
     * @param correctAnswer
     * @return
     */
    private boolean compareSlots(Map<String, Slot> slots, int amountOfAnswers, int correctAnswer) {
        for (Slot slot : slots.values()) {
            if (slot.getValue() != null) {
                int answerNumber = -1;
                if (slot.getValue().equals("?"))
                    throw new AskSdkException("Antwort des Nutzers konnte nicht erkannt werden."); //Es kommt vor, wenn man mit "Antwort ein" Antwortet, dass im value feld ein Fragezeichen steht
                if (slot.getName().equals("number")) answerNumber = Integer.valueOf(slot.getValue());
                if (slot.getName().equals("letter")) answerNumber = letterToInt(slot.getValue());
                if (answerNumber == correctAnswer) return true;
                if (answerNumber > amountOfAnswers)
                    throw new AnswerOutOfBoundsException("Antwort des Nutzers ist nicht in der Fragestellung enthalten.", amountOfAnswers); //Es gibt bspw nur 3 Antwortmöglichkeiten, der Nutzer sagt aber 4
            }
        }
        return false;
    }

    /**
     * @param letter
     * @return
     */
    public int letterToInt(String letter) {
        return letter.toLowerCase().charAt(0) - 'a' + 1;
    }

    /**
     * @param quizItem
     * @return
     */
    public int getCorrectAnswer(QuizItem quizItem) {
        int i = 1;
        for (Boolean value : quizItem.getAnswers().values()) {
            if (value.equals(Boolean.TRUE)) {
                return i;
            } else {
                i++;
            }
        }
        throw new PersistenceException("Es wurde keine richtige Antwort gefunden. Fehler in der XML Datei");
        //return quizItem.getAnswers().headMap(quizItem.getAnswers().entrySet().stream().filter(item -> item.getValue().equals(Boolean.TRUE)).findFirst().get().getKey()).size();
        //return quizItem.getAnswers().entrySet().stream().filter(item -> item.getValue().equals(Boolean.TRUE)).findFirst().get().getKey(); // Hier könnte man alle richtigen Antworten suchen, damit theoretisch auch Mulitplechoice unterstützt wird
    }

}
