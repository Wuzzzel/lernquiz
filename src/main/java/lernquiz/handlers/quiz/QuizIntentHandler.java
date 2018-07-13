package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.utils.QuestionUtils;

import static com.amazon.ask.request.Predicates.sessionAttribute;
import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;

public class QuizIntentHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.QUIZ_STATE).negate()));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt die Ausgabe der Quizfrage
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());

        //Sessiondaten aktuallisieren
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.QUIZ_STATE);
        sessionAttributes.put(Attributes.FIRST_QUESTION_KEY, true);

        //Quizfrage generieren und return
        return QuestionUtils.generateQuestionResponse(input);
    }
}