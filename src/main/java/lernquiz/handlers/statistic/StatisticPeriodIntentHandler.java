package main.java.lernquiz.handlers.statistic;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.Entry;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
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
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StatisticPeriodIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.STATISTIC_STATE)));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Behandelt die Angabe eines Zeitpunktes, zur Statistikabfrage durch die Nutzer
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

        //Daten-Slot aus der aktuellen Anfrage holen
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        String date = QuestionUtils.getSlotAnswer(intentRequest.getIntent().getSlots(), "date");

        //Unterschiedliche Ausführungen des Slot-Strings in einheitliches Date umwandeln
        //und zur Ausgabe anschließend in ein String-Format, dass Alexa gut wiedergeben kann
        Date result = stringToDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
        String dateString = sdf.format(result);

        //Daten für die Statistik sammeln und die Antwort zusammensetzen
        UserData userData = DataManager.loadUserData(input);
        List<Entry> entries = UserDataUtils.getEntriesToDate(userData, result.getTime());
        int entriesCount = entries.size();
        String responseText = "";
        if (entriesCount > 0) { //Nur wenn Einträge in der Datenbank vorhanden sind
            String correctAnsweredPercent = UserDataUtils.getCorrectAnsweredPercent(entries);
            long answeredEasyCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_EASY);
            long answeredMediumCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_MEDIUM);
            long answeredHardCount = UserDataUtils.getAnsweredDifficultyCount(entries, Constants.DIFFICULTY_INTEGER_HARD);

            //Nutzung der SSML Syntax, zur Unterstützung der richtigen Aussprache von Alexa
            responseText = "Vom " + Constants.SSML_SAYAS_DATE + dateString + Constants.SSML_SAYAS_ENDTAG
                    + " bis jetzt, hast du bereits " + entriesCount + " Fragen beantwortet. Davon waren "
                    + correctAnsweredPercent + " Prozent richtig. Außerdem hast du "
                    + Constants.SSML_SAYAS_CARDINAL + answeredHardCount + Constants.SSML_SAYAS_ENDTAG + " Fragen als Schwer markiert, "
                    + Constants.SSML_SAYAS_CARDINAL + answeredMediumCount + Constants.SSML_SAYAS_ENDTAG + " als mittelschwer und "
                    + Constants.SSML_SAYAS_CARDINAL + answeredEasyCount + Constants.SSML_SAYAS_ENDTAG + " als leicht.";
        } else { //Ansonsten gibt Nutzer darauf hinweisen, dass es keine Daten in diesem Zeitraum gibt
            responseText = "In diesem Zeitraum wurden keine Fragen beantwortet.";
        }
        //Anschließend Hauptmenü Antwort mit anhängen. Änderungen in die Session schreiben und return
        responseText += Constants.SSML_BREAK_PARAGRAPH + " " + Constants.MAIN_MENU_MESSAGE[assistMode];
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
     * Wandelt die vielen möglichen Nutzerangabe eines Zeitpunktes in den Date-Typ um und gibt diesen zurück
     *
     * @param date das übergebene Datum in String-Form
     * @return das umgewandelte Datum {@link Date}
     * @throws AskSdkException, wenn das Format des Datums (date) nicht erkannt werden konnte,
     * oder beim Parsen des Strings eine ParseException geworfen wird
     */
    public static Date stringToDate(String date) throws AskSdkException {
        /** Mögliche Formen des Parameters date:
         * value: 2019-07 <- Bei Nutereingabe: Juli (leider falsche Angabe mit 2019)
         * value: 2018-07 <- Bei Nutereingabe: Dieser Monat
         * value: 2018-07-08 <- Bei Nutereingabe: Heute, Gestern usw.
         * value: 2018-W27 <- Bei Nutereingabe: Diese Woche
         * value: 2018 <- Bei Nutereingabe: Dieses Jahr
         */
        Date result;
        String stringDateFormat = determineDateFormat(date)
                .orElseThrow(() -> new AskSdkException("Date parsing Error."));
        if (stringDateFormat.equals("WEEK_OF_YEAR")) { // Wenn es das Kalenderwochen Format ist
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.valueOf(date.substring(0, 4)));  //Parse die Daten einzeln
            cal.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(date.substring(6, date.length())));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            result = cal.getTime();
        } else { //Wenn es eins der anderen Formate ist
            DateFormat df = new SimpleDateFormat(stringDateFormat, Locale.GERMAN);
            try {
                result = df.parse(date);
            } catch (ParseException e) {
                throw new AskSdkException("Date parsing Error.");
            }
        }
        return result;
    }

    /**
     * Prüft den übergebenen String gegen definierte Datumsformate und gibt, falls gefunden,
     * das dazugehörige SimpleDateFormat pattern zurück.
     *
     * @param dateString Der Datum-String, zu dem das passende SimpleDateFormat pattern ermittelt werden soll
     * @return Optional das passende SimpleDateFormat pattern als String {@link Optional<String>}
     */
    public static Optional<String> determineDateFormat(String dateString) {
        //Regex zu den dazugehörigen SimpleDateFormat patterns anlegen
        Map<String, String> dateFormatRegex = new HashMap<String, String>() {{
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{4}-\\d{1,2}$", "yyyy-MM");
            put("^\\d{4}-w\\d{1,2}$", "WEEK_OF_YEAR");
            put("^\\d{4}$", "yyyy");
        }};
        //String gegen die Mapkeys checken
        for (String regexp : dateFormatRegex.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return Optional.of(dateFormatRegex.get(regexp)); //Wenn gefunden return
            }
        }
        return Optional.empty(); //Unbekanntes Format
    }
}
