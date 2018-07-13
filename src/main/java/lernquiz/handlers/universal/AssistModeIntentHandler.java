package main.java.lernquiz.handlers.universal;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class AssistModeIntentHandler implements RequestHandler {

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AssistModeIntent"));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Beinhaltet die Logik des Allgemeingültigen Befehls "Unterstützungsmodus"
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     * @throws AskSdkException, wenn die Angabe des Nutzers nicht zu den Utterances des Intents passen
     */
    @Override
    public Optional<Response> handle(HandlerInput input) throws AskSdkException {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        //Daten-Slot aus der aktuellen Anfrage holen, prüfen und in int umwandlen
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String wantedAssistMode = QuestionUtils.getSlotAnswer(intentRequest.getIntent().getSlots(), "deActivate");
        if (Constants.ASSIST_MODE_INTEGER_MAP.get(wantedAssistMode) == null)
            throw new AskSdkException("Antwort des Nutzers passt nicht zu den Utterances des Intents.");
        int wantedAssistModeInt = Constants.ASSIST_MODE_INTEGER_MAP.get(wantedAssistMode);

        //Antwort entsprechend des aktuellen Zustandes des Unterstützungsmodus auswählen
        String responseText = "";
        if (wantedAssistModeInt == assistMode) {
            if (wantedAssistModeInt == Constants.ASSIST_MODE_NEWBIE)
                responseText = Constants.ASSIST_MODE_IS_ALREADY_ACTIVATED; //Die Nutzer sind schon Neulinge
            else responseText = Constants.ASSIST_MODE_IS_ALREADY_DEACTIVATED; //Die Nutzer sind schon Experten
        } else {
            if (wantedAssistModeInt == Constants.ASSIST_MODE_NEWBIE)
                responseText = Constants.ASSIST_MODE_ACTIVATED; //Die Nutzer werden jetzt Neulinge
            else responseText = Constants.ASSIST_MODE_DEACTIVATED; //Die Nutzer werden jetzt Experten

            //Änderung in die Datenbank und Session schreiben
            UserData userData = DataManager.loadUserData(input);
            userData.setAssistMode(wantedAssistModeInt);
            DataManager.saveUserData(input, userData);
            sessionAttributes.put(Attributes.ASSIST_MODE, wantedAssistModeInt);
        }

        //return Antwort
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withShouldEndSession(false)
                .build();
    }
}
