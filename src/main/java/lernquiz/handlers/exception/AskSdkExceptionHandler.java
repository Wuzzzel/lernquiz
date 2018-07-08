package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.requestType;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class AskSdkExceptionHandler implements ExceptionHandler {

    /**
     * Returns true if the implementation can handle the specified throwable
     *
     * @param input     handler input
     * @param throwable exception
     * @return boolean
     */
    @Override
    public boolean canHandle(HandlerInput input, Throwable throwable) {
        return throwable instanceof AskSdkException;
    }

    /**
     * Handles the exception
     *
     * @param input     handler input
     * @param throwable exception
     * @return handler output
     */
    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();

        // TODO: Kann der zähler zum defnieren des Neuling, Fortgeschrittenen zeug genutzt werden?
        int grammarExceptionsCount = (int) sessionAttributes.get(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, grammarExceptionsCount + 1);

        QuestionUtils.logHandling(input, this.getClass().getName());

        if (grammarExceptionsCount < Constants.GRAMMAR_ERROR.size()) {
            return QuestionUtils.generateUniversalOrExceptionResponse(input, Constants.GRAMMAR_ERROR.get(grammarExceptionsCount), false);
        } else {
            return input.getResponseBuilder()
                    .withSpeech(Constants.GRAMMAR_ERROR_MESSAGE)
                    .withShouldEndSession(true)
                    .build();
        }
    }
}