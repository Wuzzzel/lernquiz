package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.utils.QuestionUtils;

import static com.amazon.ask.request.Predicates.sessionAttribute;
import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class QuizIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.QUIZ_STATE).negate()));
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

        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.QUIZ_STATE);
        sessionAttributes.put(Attributes.FIRST_QUESTION_KEY, true);

        return QuestionUtils.generateQuestionResponse(input);
    }
}
