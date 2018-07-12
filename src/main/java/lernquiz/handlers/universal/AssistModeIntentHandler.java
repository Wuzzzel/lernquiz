package main.java.lernquiz.handlers.universal;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.handlers.quiz.QuizDifficultyIntentHandler;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class AssistModeIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AssistModeIntent"));
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
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);

        //Slot checken
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String wantedAssistMode = QuizDifficultyIntentHandler.getSlotAnswer(intentRequest.getIntent().getSlots(), "deActivate");
        //if(Constants.ASSIST_MODE_INTEGER_MAP.get(wantedAssistMode) == null) throw new AskSdkException("Antwort des Nutzers passt nicht zu den Utterances des Intents.");
        int wantedAssistModeInt = Constants.ASSIST_MODE_INTEGER_MAP.get(wantedAssistMode);

        String responseText = "";
        if(wantedAssistModeInt == assistMode){
            if(wantedAssistModeInt == Constants.ASSIST_MODE_NEWBIE) responseText = Constants.ASSIST_MODE_IS_ALREADY_ACTIVATED; //Der nutzer ist schon Newbie
            else  responseText = Constants.ASSIST_MODE_IS_ALREADY_DEACTIVATED;    //Der Nutzer ist schon Experte
        }else{
            if(wantedAssistModeInt == Constants.ASSIST_MODE_NEWBIE) responseText = Constants.ASSIST_MODE_ACTIVATED; //Der Nutzer ist nun Newbie
            else  responseText = Constants.ASSIST_MODE_DEACTIVATED;    //Der Nutzer ist nun Experte

            //Änderung in Datenbank schreiben
            UserData userData = DataManager.loadUserData(input);
            userData.setAssistMode(wantedAssistModeInt);
            DataManager.saveUserData(input, userData);
            //Und is Session schreiben
            sessionAttributes.put(Attributes.ASSIST_MODE, wantedAssistModeInt);
        }

        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withShouldEndSession(false)
                .build();
    }

}
