package main.java.lernquiz.handlers.statistic;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class StatisticIntentHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StatisticIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.START_STATE)));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Beinhaltet die Logik zur Abfrage des Zeitraumes der zu ermittelnden Statistik
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        //Daten dieses Intents in die Session schreiben. Antwort-String finalisieren und return
        String responseText = Constants.STATISTIC_QUESTION_MESSAGE[assistMode];
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.STATISTIC_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.STATISTIC_QUESTION_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }
}
