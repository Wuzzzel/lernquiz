package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.AnswerOutOfBoundsException;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AnswerOutOfBoundsHandler implements ExceptionHandler {

    /**
     * Returns true if the implementation can handle the specified throwable
     *
     * @param input     handler input
     * @param throwable exception
     * @return boolean
     */
    @Override
    public boolean canHandle(HandlerInput input, Throwable throwable) {
        return throwable instanceof AnswerOutOfBoundsException;
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
        QuestionUtils.logHandling(input, this.getClass().getName());

        AnswerOutOfBoundsException exp = (AnswerOutOfBoundsException) throwable;
        int amountOfAnswers = exp.getAmountOfAnswers();
        return input.getResponseBuilder()
                .withSpeech(Constants.EXCEPTION_ANSWER_OUT_OF_BOUNDS_FIRST_MESSAGE + amountOfAnswers + Constants.EXCEPTION_ANSWER_OUT_OF_BOUNDS_SECOND_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }
}
