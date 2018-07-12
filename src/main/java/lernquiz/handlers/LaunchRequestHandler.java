package main.java.lernquiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.User;
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
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

    /**
     * Accepts an input and generates a response
     *
     * @param input request envelope containing request, context and state
     * @return an optional {@link Response} from the handler.
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuestionUtils.logHandling(input, this.getClass().getName());

        UserData userData = DataManager.loadUserData(input);
        int assistMode = userData.getAssistMode();
        sessionAttributes.put(Attributes.ASSIST_MODE, assistMode); //Unterstützungsmodus in Session schreiben, damit nicht in jedem Handler auf die Datenbank zugegriffen werden muss

        sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        sessionAttributes.put(Attributes.LAST_QUIZ_ITEM_KEY, ""); //Wird hier schonmal gesetzt, damit später beim ersten get keine null abfrage gemacht werden muss

        // TODO: Für First Time User noch eine weitere Ausgabe einbauen -> Feststellen, indem man in die Datenbank schaut ob der User schon Fragen beantwortet hat
        // TODO: Aus Datenbank bekommen ob der User ein Newbie oder Advanced ist und dem entsprechend Ausgaben ändern

        String responseText = Constants.WELCOME_MESSAGE + " " + Constants.MAIN_MENU_INITIAL_MESSAGE[assistMode];
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText); //Wird hier gespeichert, damit es für das Universal Wiederholen bereit gestellt ist
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }

}