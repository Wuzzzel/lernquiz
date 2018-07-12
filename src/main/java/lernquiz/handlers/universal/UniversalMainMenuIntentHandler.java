package main.java.lernquiz.handlers.universal;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class UniversalMainMenuIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("UniversalMainMenuIntent"));
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
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        String responseText;
        String state = (String) sessionAttributes.get(Attributes.STATE_KEY);
        if(state.equals(Attributes.START_STATE)) {
            responseText = Constants.UNIVERSAL_MAIN_MENU_MESSAGE; // Wenn der Nutzer bereits im Hauptmenü ist, bekommt er die Repsonse er sei schon dort
        } else {
            responseText = Constants.MAIN_MENU_MESSAGE[assistMode];   //TODO: Das ist hier so ziemlich das gleiche wie in der QuizAnotherQuestionIntentHandler geschichte
        }
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
        sessionAttributes.put(Attributes.RESPONSE_KEY, Constants.MAIN_MENU_MESSAGE[assistMode]); //Beim Universal Wiederhole, soll aber die normale Hauptmenü Response folgen
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }
}
