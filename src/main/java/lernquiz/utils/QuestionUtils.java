package main.java.lernquiz.utils;

import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.Questions;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.model.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class QuestionUtils {

    static final Logger logger = LogManager.getLogger(QuestionUtils.class);

    /**
     * Generiert eine Quizfrage und gibt diese als optionale Antwort (Response) zurück
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return optionale Antwort (Response) {@link Optional<Response>}
     */
    public static Optional<Response> generateQuestionResponse(HandlerInput input) {
        //Daten aus Session und Quizfragen aus XML holen
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);
        boolean isFirstQuestion = (boolean) sessionAttributes.get(Attributes.FIRST_QUESTION_KEY);
        Questions questions = DataManager.loadQuestions();

        //Falls es die erste Quizfrage ist, füge dem Antworttext eine Ausgabe zur Quiz-Kategorie hinzu
        String responseText = "";
        if (isFirstQuestion) {
            responseText = Constants.QUIZ_CATEGORY_MESSAGE + questions.getCategory() + ". Viel Erfolg! ";
            sessionAttributes.put(Attributes.FIRST_QUESTION_KEY, false);
        }

        //Nächste Quizfrage evaluieren, dazu werden die letzte Quizfragen-ID und Nutzerdaten benötigt
        String lastQuestionID = (String) sessionAttributes.get(Attributes.LAST_QUIZ_ITEM_KEY);
        UserData userData = DataManager.loadUserData(input);
        QuizItem quizItem = getNextQuestion(userData, questions, lastQuestionID);

        //Speichern der Intentdaten in der Session und generieren des Quizfragentextes
        sessionAttributes.put(Attributes.QUIZ_ITEM_KEY, quizItem);
        responseText += getQuestionText(quizItem);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText); //Wird hier gespeichert, damit es für das Universal "Wiederholen" bereit gestellt ist

        //return der Antwort
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_QUESTION_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }

    /**
     * Gibt nach Beachtung der übergebenenen Attribute, ein passendes Quizitem zurück. Geht nach folgender Logik vor:
     * Wenn vorhanden wähle ein zufälliges Quizitem aus der Kategorie Schwer
     * Ansonsten wähle wenn vorhanden ein zufälliges Quizitem, dass noch nicht beantwortet wurde
     * Ansonsten wähle wenn vorhanden ein zufälliges Quizitem aus der Kategorie Mittel
     * Ansonsten wähle wenn vorhanden ein zufälliges Quizitem aus der Kategorie Leicht
     * Ansonsten wähle ein zufälliges Quizitem
     *
     * @param userData       aus den die bereits beantworteten Quizfragen des Nutzers genommen werden
     * @param questions      alle zur verfügung stehenden Quizitems
     * @param lastQuestionID die Id der zuletzt beantworteten Quizfrage
     * @return das zur ermittelten Quizfrage gehörende Quizitem {@link QuizItem}
     */
    public static QuizItem getNextQuestion(UserData userData, Questions questions, String lastQuestionID) {
        Random random = new Random();
        List<String> hardQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_HARD, lastQuestionID);
        if (!hardQuestions.isEmpty()) { //Prüfe zunächst ob es Fragen gibt, die vom Nutzer zuletzt mit Schwer markiert wurden
            return getRandomQuizItem(questions, hardQuestions);
        }
        Set<String> answeredQuestions = userData.getQuestions().keySet();
        List<String> unansweredQuestions = questions.getQuestionsMap().keySet().parallelStream().filter(questionId -> !answeredQuestions.contains(questionId)).collect(Collectors.toList()); //Filter nur Fragen heraus, die noch nicht beantwortet wurden
        if (!unansweredQuestions.isEmpty()) { //Ansonsten prüfe ob es Fragen gibt, die vom Nutzer noch nicht beantwortet wurden
            return getRandomQuizItem(questions, unansweredQuestions); //nimm eine Frage die noch nicht dran kamm
        }
        List<String> mediumQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_MEDIUM, lastQuestionID);
        if (!mediumQuestions.isEmpty()) { //Ansonsten prüfe ob es Fragen gibt, die vom Nutzer zuletzt mit Mittel markiert wurden
            return getRandomQuizItem(questions, mediumQuestions);
        }
        List<String> easyQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_EASY, lastQuestionID);
        if (!easyQuestions.isEmpty()) { //Ansonsten prüfe ob es Fragen gibt, die vom Nutzer zuletzt mit Leicht markiert wurden
            return getRandomQuizItem(questions, easyQuestions);
        }
        List<QuizItem> quizItems = new ArrayList<>(questions.getQuestionsMap().values()); //Wenn alle Listen leer sind, nimm eine Random Frage -> Quasi nur zu beginn des lernens
        return quizItems.get(random.nextInt(quizItems.size())); //Nimm eine zufällige Frage
    }

    /**
     * Gibt die ein zufälliges Quizitem, mit einer questionId, die in der übergebenen Liste (toBeInspectedQuestions) vorhanden ist, zurück
     *
     * @param questions              enthält die Quizitems
     * @param toBeInspectedQuestions Liste an zu beachtenden questionIds
     * @return zufälliges Quizitem, dass eine questionId aus toBeInspectedQuestions besitzt {@link QuizItem}
     */
    public static QuizItem getRandomQuizItem(Questions questions, List<String> toBeInspectedQuestions) {
        Random random = new Random();
        String questionId = toBeInspectedQuestions.get(random.nextInt(toBeInspectedQuestions.size()));
        return questions.getQuestionsMap().get(questionId);
    }

    /**
     * Gibt den Ausgabetext des übergebenen Quizitems als String zurück.
     * Besteht aus der Fragestellung und den Antwortmöglichkeiten
     *
     * @param quizItem aus dem die Ausgabe erstellt werden soll
     * @return String Ausgabetext {@link String}
     */
    public static String getQuestionText(QuizItem quizItem) {
        return Constants.QUIZ_QUESTION_MESSAGE + quizItem.getQuestion() + Constants.SSML_BREAK_SENTENCE + " " + Constants.QUIZ_ANSWER_OPTIONS_MESSAGE +
                getAnswersWithIsolator(quizItem.getAnswers()).stream().collect(Collectors.joining(". "));
    }

    /**
     * Stellt den übergebenen Strings aus der Liste (answers) jeweils ein "Antwort " + fortlaufender
     * Buchstaben Nummerierung vor, beginnend bei A
     *
     * @param answers Liste, dessen Strings verarbeitet werden sollen
     * @return Liste mit Strings, die fortlaufend nummeriert wurden {@link List<String>}
     */
    public static List<String> getAnswersWithIsolator(List<String> answers) {
        char answerLetter = 'A';
        List<String> answersWithIsolator = answers;
        for (int i = 0; i < answersWithIsolator.size(); i++) {
            answersWithIsolator.set(i, "Antwort " + answerLetter++ + ": " + answersWithIsolator.get(i));
        }
        return answersWithIsolator;
    }

    /**
     * Logt neben den übergebenen Klassennamen (className), Daten aus dem input. Dazu gehört der STATE_KEY
     * und wenn der input ein IntenRequest beinhaltet, wird dessen Name gelogt. Außerdem, wenn vorhanden, die
     * dort enthaltenen Slots
     *
     * @param input     Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param className Klassenname, der dem erstellten Log vorgestellt wird
     */
    public static void logHandling(HandlerInput input, String className) {
        logger.debug(className);
        logger.debug("State_Key: " + input.getAttributesManager().getSessionAttributes().get(Attributes.STATE_KEY));
        if (input.getRequestEnvelope().getRequest().getType().equals("IntentRequest")) { //Beim Start des Skills, ist bspw. ein "LaunchRequest" und besitzt dann kein Intent
            IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
            logger.debug("IntentName: " + intentRequest.getIntent().getName());
            if (intentRequest.getIntent().getSlots() != null)
                intentRequest.getIntent().getSlots().values().parallelStream().filter(slot -> Objects.nonNull(slot)).forEach(slot -> logger.debug("Slots: " + slot));
        }
    }

    /**
     * Holt den Zustand aus dem, übergebenen HandlerInput und wählt damit aus der übergebenen LinkedHashMap (responseText)
     * ein Antworttext aus. Aus dieser und dem Parameter shouldEndSession, wird anschließend eine optionale
     * Antwort erstellt und zurück gegeben
     *
     * @param input            Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param responseText     LinkedHashMap die die Antworttexte assoziert mit Zuständen enthält
     * @param shouldEndSession gibt an ob die Session mit dieser Antwort beendet werden soll
     * @return die generierte optionale Antwort {@link Optional<Response>}
     */
    public static Optional<Response> generateUniversalOrExceptionResponse(HandlerInput input, LinkedHashMap<String, String> responseText, boolean shouldEndSession) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        String currentState = (String) sessionAttributes.get(Attributes.STATE_KEY);
        return Arrays.stream(Attributes.STATES).filter(state -> state.equals(currentState)).findFirst()
                .map(state -> buildResponse(input, responseText.get(state), shouldEndSession))
                .orElse(buildResponse(input, Constants.GRAMMAR_ERROR_MESSAGE, shouldEndSession));
    }

    /**
     * Generiert aus dem übergebenen Antworttext (responseText) und dem Parameter shouldEndSession eine optionale Antwort
     *
     * @param input            Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @param responseText     String der den Antworttext enthält
     * @param shouldEndSession gibt an ob die Session mit dieser Antwort beendet werden soll
     * @return die generierte optionale Antwort {@link Optional<Response>}
     */
    public static Optional<Response> buildResponse(HandlerInput input, String responseText, boolean shouldEndSession) {
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withShouldEndSession(shouldEndSession)
                .build();
    }

    /**
     * Gibt aus der übergebenen Map (slots) den Inhalt des Slots zurück, dessen Name gleich dem des Parameters slotName ist
     *
     * @param slots    Map an Slots, die durchsucht werden sollen
     * @param slotName Name des Slots, dessen Inhalt zurückgegeben werden soll
     * @return Inhalt des gesuchten Slots {@link String}
     * @throws AskSdkException, wenn die Slots leer sind, der slotName nicht gefunden wurde oder wenn inerhalb der Antwort
     *                          der StatusCode "ER_SUCCESS_NO_MATCH" gesetzt ist
     */
    public static String getSlotAnswer(Map<String, Slot> slots, String slotName) throws AskSdkException {
        for (Slot slot : slots.values()) {
            // Indication of the results of attempting to resolve the user utterance against the defined slot types
            //Bedeutet: Das Wort das vom Nutzer gesagt wurde, wurde zwar erkannt, aber passt nicht in den definierten slot des Intents
            if (slot.getResolutions() != null && slot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode().equals(StatusCode.ER_SUCCESS_NO_MATCH)) {
                throw new AskSdkException("Antwort des Nutzers passt nicht zu den Utterances des Intents.");
            }
            if (slot.getValue() != null && slot.getName().equals(slotName)) {
                return slot.getValue();
            }
        }
        throw new AskSdkException("Keine Daten in Slot vorhanden.");
    }
}
