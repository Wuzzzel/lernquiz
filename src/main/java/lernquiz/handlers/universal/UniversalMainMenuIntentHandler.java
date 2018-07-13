package main.java.lernquiz.handlers.universal;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class UniversalMainMenuIntentHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("UniversalMainMenuIntent"));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Beinhaltet die Logik des Allgemeingültigen Befehls "Hauptmenü"
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

        //Befehl Logik
        String responseText;
        String state = (String) sessionAttributes.get(Attributes.STATE_KEY);
        if (state.equals(Attributes.START_STATE)) {
            responseText = Constants.UNIVERSAL_MAIN_MENU_MESSAGE; //Wenn die Nutzer sich bereits im Hauptmenü befinden, bekommen sie die Antwort sie seien schon dort
        } else {
            responseText = Constants.MAIN_MENU_MESSAGE[assistMode]; //Ansonsten Ausgabe der Hauptmenü-Antwort
        }

        //Daten dieses Intents in die Session schreiben. Antwort-String finalisieren und return
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
        sessionAttributes.put(Attributes.RESPONSE_KEY, Constants.MAIN_MENU_MESSAGE[assistMode]); //Beim Universal Wiederhole, soll die normale Hauptmenü-Antwort folgen
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }
}
