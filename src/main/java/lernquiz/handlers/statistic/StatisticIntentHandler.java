package main.java.lernquiz.handlers.statistic;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class StatisticIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StatisticIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.START_STATE)));
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


        String responseText = Constants.STATISTIC_QUESTION_NEWBIE_MESSAGE;
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.STATISTIC_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.STATISTIC_QUESTION_REPROMT_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }
}
