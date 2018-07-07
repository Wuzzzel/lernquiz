package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.exception.PersistenceException;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Optional;

public class PersistenceExceptionHandler implements ExceptionHandler {

    static final Logger logger = LogManager.getLogger(PersistenceExceptionHandler.class);

    @Override
    public boolean canHandle(HandlerInput input, Throwable throwable) {
        return throwable instanceof PersistenceException;
    }

    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        QuestionUtils.logHandling(input, this.getClass().getName());
        logger.error(throwable);
        return input.getResponseBuilder()
                .withSpeech(Constants.EXCEPTION_PERSISTENCE_MESSAGE)
                .build();
    }
}