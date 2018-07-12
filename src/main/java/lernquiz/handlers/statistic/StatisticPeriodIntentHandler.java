package main.java.lernquiz.handlers.statistic;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.handlers.quiz.QuizDifficultyIntentHandler;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.utils.QuestionUtils;
import main.java.lernquiz.utils.UserDataUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class StatisticPeriodIntentHandler implements RequestHandler {

    /**
     * Returns true if the handler can dispatch the current request
     *
     * @param input request envelope containing request, context and state
     * @return true if the handler can dispatch the current request
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StatisticPeriodIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.STATISTIC_STATE)));
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

        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String date = QuizDifficultyIntentHandler.getSlotAnswer(intentRequest.getIntent().getSlots(), "date"); //Todo die Methode getSlotAnswer() wo anders hin packen

        /**
         * name: date
         * value: 2019-07 <- juli (leider falsche angabe mit 2019)
         * value: 2018-07 <- dieser Monat
         * value: 2018-07-08 <- heute, gestern usw.
         * value: 2018-W27 <- diese Woche
         * value: 2018 <- dieses Jahr
         */
        Date result;
        String stringDateFormat = determineDateFormat(date).orElseThrow(() -> new AskSdkException("Date parsing Error."));
        if(stringDateFormat.equals("WEEK_OF_YEAR")){ // Wenn es das Kalenderwochen Format ist
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.valueOf(date.substring(0, 4)));
            cal.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(date.substring(6, date.length())));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            result = cal.getTime();
        } else { // Wenn es eins der normalen Formate ist
            DateFormat df = new SimpleDateFormat(stringDateFormat, Locale.GERMAN);
            try {
                result = df.parse(date);
            } catch (ParseException e) {
                throw new AskSdkException("Date parsing Error.");
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
        String dateString = sdf.format(result);

        //Daten für die Ausgabe sammeln
        UserData userData = DataManager.loadUserData(input);
        List<Entry> entries = UserDataUtils.getEntriesToDate(userData, result.getTime());
        int entriesCount = entries.size();
        String responseText = "";
        if(entriesCount > 0) {
            String correctAnsweredPercent = UserDataUtils.getCorrectAnsweredPercent(entries);
            long answeredEasyCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_EASY);
            long answeredMediumCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_MEDIUM);
            long answeredHardCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_HARD);

            responseText = "Vom " + Constants.SSML_SAYAS_DATE + dateString + Constants.SSML_SAYAS_ENDTAG
                    + " bis jetzt, hast du bereits " + entriesCount + " Fragen beantwortet. Davon waren "
                    + correctAnsweredPercent + " Prozent richtig. Außerdem hast du "
                    + Constants.SSML_SAYAS_CARDINAL + answeredHardCount + Constants.SSML_SAYAS_ENDTAG + " Fragen als Schwer markiert, "
                    + Constants.SSML_SAYAS_CARDINAL + answeredMediumCount + Constants.SSML_SAYAS_ENDTAG + " als mittelschwer und "
                    + Constants.SSML_SAYAS_CARDINAL + answeredEasyCount + Constants.SSML_SAYAS_ENDTAG + " als leicht.";
        } else{
            responseText = "In diesem Zeitraum wurden keine Fragen beantwortet.";
        }
        responseText +=  Constants.SSML_BREAK_PARAGRAPH + " " + Constants.MAIN_MENU_MESSAGE[assistMode];
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.START_STATE);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.MAIN_MENU_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }


    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     * @see SimpleDateFormat
     */
    public static Optional<String> determineDateFormat(String dateString) {
        Map<String, String> dateFormatRegex = new HashMap<String, String>() {{
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{4}-\\d{1,2}$", "yyyy-MM");
            put("^\\d{4}-w\\d{1,2}$", "WEEK_OF_YEAR");
            put("^\\d{4}$", "yyyy");
        }};

        for (String regexp : dateFormatRegex.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return Optional.of(dateFormatRegex.get(regexp));
            }
        }
        return Optional.empty(); // Unknown format.
    }
}
