package main.java.lernquiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class LaunchRequestHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt die Nutzerbegrüßung und Ausgabe des Hauptmenüs
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session und Datenbank holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());
        UserData userData = DataManager.loadUserData(input);
        int assistMode = userData.getAssistMode();
        //Unterstützungsmodus in Session schreiben, damit nicht in jedem Handler auf die
        // Datenbank zugegriffen werden muss
        sessionAttributes.put(Attributes.ASSIST_MODE, assistMode);

        //Antwort-String mit Willkommensnachricht vorbereiten
        String responseText = Constants.WELCOME_MESSAGE + " ";
        //Schauen ob der Nutzer ganz neu im Skill ist, dann extra Nachricht ausgeben
        if (userData.getQuestions().size() == 0) responseText += Constants.FIRST_TIME_USER_MESSAGE + " ";

        //Daten dieses Intents in die Session schreiben
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        //LAST_QUIZ_ITEM_KEY wird hier schonmal gesetzt, damit später beim ersten get(),
        // auf das Attribut, keine null-Abfrage gemacht werden muss
        sessionAttributes.put(Attributes.LAST_QUIZ_ITEM_KEY, "");

        //Antwort-String finalisieren, in Session schreiben und return
        responseText += Constants.MAIN_MENU_INITIAL_MESSAGE[assistMode];
        //RESPONSE_KEY wird hier gespeichert, damit es für das Universal-Wiederholen bereit gestellt ist
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }
}