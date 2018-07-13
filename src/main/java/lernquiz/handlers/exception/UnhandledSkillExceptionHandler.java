package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.UnhandledSkillException;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Optional;

public class UnhandledSkillExceptionHandler implements ExceptionHandler {

    static final Logger logger = LogManager.getLogger(UnhandledSkillExceptionHandler.class);

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input     Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param throwable geworfene Exception
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input, Throwable throwable) {
        return throwable instanceof UnhandledSkillException;
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt das Geschehen bei dem auftreten einer UnhandledSkillException
     *
     * @param input     Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param throwable geworfene Exception
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        //Logge den aktuellen Zustand
        QuestionUtils.logHandling(input, this.getClass().getName());
        logger.error(throwable);

        //Return Fehlerausgabe an Nutzer
        return input.getResponseBuilder()
                .withSpeech(Constants.EXCEPTION_UNHANDLED_SKILL_MESSAGE)
                .build();
    }
}
