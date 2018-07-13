package main.java.lernquiz.handlers.quiz;

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

public class QuizAnotherQuestionIntentHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.YesIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE))) ||
                input.matches(intentName("AMAZON.NoIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.ANOTHER_QUESTION_STATE)));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Verarbeitet die Nutzerangaben bezüglich einer neuen Quizfrage
     * und gibt die entsprechende Antwort zurück (Hauptmenü oder neue Quizfrage)
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);
        QuestionUtils.logHandling(input, this.getClass().getName());
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);

        if (input.matches(intentName("AMAZON.YesIntent"))) { //Wenn neue Quizfrage gewünscht, generieren und return
            sessionAttributes.put(Attributes.STATE_KEY, Attributes.QUIZ_STATE);
            sessionAttributes.put(Attributes.RESPONSE_KEY, "");
            return QuestionUtils.generateQuestionResponse(input);
        } else { //Wenn keine neue Quizfrage gewünscht, Hauptmenü-Antwort und return
            String responseText = randomJustInTimeTip(assistMode) + " " + Constants.MAIN_MENU_MESSAGE[assistMode];
            sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
            sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
            return input.getResponseBuilder()
                    .withSpeech(responseText)
                    .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                    .withShouldEndSession(false)
                    .build();
        }
    }

    /**
     * Generiert zufällig einen Just-in-Time Tipp und gibt ihn zurück
     *
     * @param assistMode gibt an ob der Unterstützungsmodus aktiviert ist
     * @return Just-in-Time Tipp, oder leerer String {@link String}
     */
    public static String randomJustInTimeTip(int assistMode) {
        boolean randomBoolean = Math.random() < 0.2; // 1 zu 5 Chance
        if (randomBoolean && assistMode == Constants.ASSIST_MODE_NEWBIE) return Constants.JUST_IN_TIME_EXPERT_MODE;
        else return "";
    }
}