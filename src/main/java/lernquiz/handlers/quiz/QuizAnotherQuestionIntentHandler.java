package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class QuizAnotherQuestionIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.YesIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE))) ||
                input.matches(intentName("AMAZON.NoIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE)));
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
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        QuestionUtils.logHandling(input, this.getClass().getName());
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);

        if(input.matches(intentName("AMAZON.YesIntent"))){ // Neue Frage generieren
            sessionAttributes.put(Attributes.STATE_KEY, Attributes.QUIZ_STATE);
            sessionAttributes.put(Attributes.RESPONSE_KEY, "");

            return QuestionUtils.generateQuestionResponse(input);
        } else{ // No Intent -> Hauptmenü Nachricht ausgeben
            String responseText = Constants.MAIN_MENU_MESSAGE[assistMode];
            sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
            sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
            return input.getResponseBuilder()
                    .withSpeech(responseText)
                    .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                    .withShouldEndSession(false)
                    .build();
        }
    }
}
