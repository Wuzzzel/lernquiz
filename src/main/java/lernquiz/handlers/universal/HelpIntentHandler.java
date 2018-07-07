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

public class HelpIntentHandler implements RequestHandler {

    /** canHandle, which is called by the SDK to determine if the given handler is capable of processing the incoming request.
     *  This method returns true if the handler can handle the request, or false if not.
     *
     * @param input
     * @return
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    /** handle, which is called by the SDK when invoking the request handler.
     * This method contains the handler’s request processing logic, and returns an optional Response.
     *
     * @param input
     * @return
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        QuestionUtils.logHandling(input, this.getClass().getName());

        return QuestionUtils.generateUniversalOrExceptionResponse(input, Constants.UNIVERSAL_HELP_MESSAGES, false);
    }
}