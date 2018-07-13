package main.java.lernquiz.handlers.exception;

import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

public class AskSdkExceptionHandler implements ExceptionHandler {

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
        return throwable instanceof AskSdkException;
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt das Geschehen bei dem auftreten einer AskSdkException
     * In den meisten Fällen, findet diese Exception anwendung bei einem auftretenden Grammatikfehler
     *
     * @param input     Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param throwable geworfene Exception
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input, Throwable throwable) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        //Hole Anzahl der bisherigen Grammatikfehler aus der Session, inkrementiere und speichere zurück in Session
        int grammarExceptionsCount = (int) sessionAttributes.get(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, grammarExceptionsCount + 1);

        //Wenn Grammatikfehlerausgaben noch nicht über definierten Wert, generiere eine Antwort und return
        if (grammarExceptionsCount < Constants.GRAMMAR_ERROR.get(assistMode).size()) {
            return QuestionUtils.generateUniversalOrExceptionResponse(input, Constants.GRAMMAR_ERROR.get(assistMode).get(grammarExceptionsCount), false);
        } else {
            //Wenn die Anzahl der Grammatikfehlerausgaben über den definierten Wert liegt, return und beende Skill
            return input.getResponseBuilder()
                    .withSpeech(Constants.GRAMMAR_ERROR_MESSAGE)
                    .withShouldEndSession(true)
                    .build();
        }
    }
}