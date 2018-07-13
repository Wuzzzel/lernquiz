package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.AnswerOutOfBoundsException;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Optional;

public class AnswerOutOfBoundsHandler implements ExceptionHandler {

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
        return throwable instanceof AnswerOutOfBoundsException;
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt das Geschehen bei dem auftreten einer AnswerOutOfBoundsException.
     * Findet Anwendung, wenn die Nutzer eine Antwort nennen, die nicht im möglichen Antwortbereich liegt
     *
     * @param input     Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param throwable geworfene Exception
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        //Logge den aktuellen Zustand
        QuestionUtils.logHandling(input, this.getClass().getName());

        //Hole Anzahl der Antwortmöglichkeiten aus der Exception
        AnswerOutOfBoundsException exp = (AnswerOutOfBoundsException) throwable;
        int amountOfAnswers = exp.getAmountOfAnswers();

        //Return Fehlerausgabe, mit Hilfestellung wie viele Antwortmöglichkeiten vorhanden sind
        return input.getResponseBuilder()
                .withSpeech(Constants.EXCEPTION_ANSWER_OUT_OF_BOUNDS_FIRST_MESSAGE + amountOfAnswers + Constants.EXCEPTION_ANSWER_OUT_OF_BOUNDS_SECOND_MESSAGE)
                .withShouldEndSession(false)
                .build();
    }
}
