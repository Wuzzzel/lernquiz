package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class QuizDifficultyHandler implements RequestHandler {

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

        // TODO: slotAnswer in Datenbank speichern
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String antwort = getSlotAnswer(intentRequest.getIntent().getSlots());

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
    private String getSlotAnswer(Map<String, Slot> slots) {
        for (Slot slot : slots.values()) {

            // Indication of the results of attempting to resolve the user utterance against the defined slot types
            //Bedeutet: Das Wort das vom Nutzer gesagt wurde, wurde zwar erkannt, aber passt nicht in den definierten slot des Intents
            if(slot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode().equals(StatusCode.ER_SUCCESS_NO_MATCH)) {
                throw new AskSdkException("Antwort des Nutzers passt nicht zu den Utterances des Intents.");
            }
            if(slot.getValue() != null && slot.getName().equals("difficulty")){
                return slot.getValue();
            }
        }
        throw new AskSdkException("Keine Daten in Slot vorhanden."); //Exception, da Slots leer sind
    }
}
