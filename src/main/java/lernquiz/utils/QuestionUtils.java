package main.java.lernquiz.utils;

import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

import main.java.lernquiz.dao.DataManager;
import main.java.lernquiz.dao.dynamoDbModel.UserData;
import main.java.lernquiz.dao.xmlModel.Questions;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.model.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class QuestionUtils {

    static final Logger logger = LogManager.getLogger(QuestionUtils.class);

    /**
     *
     * @param input
     * @return
     */
    public static Optional<Response> generateQuestionResponse(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);
        boolean isFirstQuestion = (boolean) sessionAttributes.get(Attributes.FIRST_QUESTION_KEY);

        String responseText = "";
        Questions questions = DataManager.loadQuestions(); //Fragen aus xml datei auslesen
        if (isFirstQuestion) {
            responseText = Constants.QUIZ_CATEGORY_MESSAGE + questions.getCategory() + ". Viel Erfolg! ";
            sessionAttributes.put(Attributes.FIRST_QUESTION_KEY, false);
        }

        // Nächste Quizfrage evaluieren
        String lastQuestionID = (String) sessionAttributes.get(Attributes.LAST_QUIZ_ITEM_KEY); //Die letzte Frage-ID laden, wird als check genutzt, damit die letzte Frage nicht direkt noch mal gewählt wird
        UserData userData = DataManager.loadUserData(input);
        QuizItem quizItem = getNextQuestion(userData, questions, lastQuestionID);

        sessionAttributes.put(Attributes.QUIZ_ITEM_KEY, quizItem);
        responseText += getQuestionText(quizItem);
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText); //Wird hier gespeichert, damit es für das Universal Wiederholen bereit gestellt ist

        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_QUESTION_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }


    public static QuizItem getNextQuestion(UserData userData, Questions questions, String lastQuestionID){
        Random random = new Random();
        List<String> hardQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_HARD, lastQuestionID);
        if(!hardQuestions.isEmpty()){ //Prüfe zunächst ob es Fragen gibt, die vom Nutzer zuletzt mit Schwer markiert wurden
            return getRandomQuizItem(questions, hardQuestions);
        }
        Set<String> answeredQuestions = userData.getQuestions().keySet();
        List<String> unansweredQuestions = questions.getQuestionsMap().keySet().parallelStream().filter(item -> !answeredQuestions.contains(item)).collect(Collectors.toList()); //Filter nur Fragen heraus, die noch nicht beantwortet wurden
        if(!unansweredQuestions.isEmpty()){
            return getRandomQuizItem(questions, unansweredQuestions); //nimm eine Frage die noch nicht dran kamm
        }
        List<String> mediumQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_MEDIUM, lastQuestionID);
        if(!mediumQuestions.isEmpty()){    //Ansonsten
            return getRandomQuizItem(questions, mediumQuestions); //gib eine der Mittelschweren Fragen zurück
        }
        List<String> easyQuestions = UserDataUtils.getCorrespondingQuestionIDs(userData, Constants.DIFFICULTY_INTEGER_EASY, lastQuestionID);
        if(!easyQuestions.isEmpty()){
            return getRandomQuizItem(questions, easyQuestions);
        }
        List<QuizItem> quizItems = new ArrayList<>(questions.getQuestionsMap().values());   //Wenn alle Listen leer sind, nimm eine Random Frage -> Quasi nur zu beginn des lernens
        QuizItem randomQuizItem = quizItems.get(random.nextInt(quizItems.size()));
        return randomQuizItem;
    }


    public static QuizItem getRandomQuizItem(Questions questions, List<String> toBeInspectedQuestions){
        Random random = new Random();
            String questionId =  toBeInspectedQuestions.get(random.nextInt(toBeInspectedQuestions.size()));
            return questions.getQuestionsMap().get(questionId);
    }


    /**
     *
     * @param quizItem
     * @return
     */
    public static String getQuestionText(QuizItem quizItem) {

        return Constants.QUIZ_QUESTION_MESSAGE + quizItem.getQuestion() + Constants.SSML_BREAK_SENTENCE + " " + Constants.QUIZ_ANSWER_OPTIONS_MESSAGE +
                getAnswersWithIsolator(quizItem.getAnswers()).stream().collect(Collectors.joining(". "));
    }


    public static List<String> getAnswersWithIsolator(List<String> answers){
        char answerLetter = 'A';
        List<String> answersWithIsolator = answers;
        for(int i = 0; i < answersWithIsolator.size(); i++){
            answersWithIsolator.set(i, "Antwort " + answerLetter++ + ": " + answersWithIsolator.get(i));
        }
        return answersWithIsolator;
    }


    /**
     *
     * @param input
     * @return
     */
    public static Optional<Response> generateDifficultyResponse(HandlerInput input) { // Dialog 04: Schwierigkeit Abfragen
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);
        String responseText = (String) sessionAttributes.get(Attributes.RESPONSE_KEY);

        responseText += " " + Constants.QUIZ_DIFFICULTY_MESSAGE[assistMode];
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);

        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_DIFFICULTY_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }

    /**
     *
     * @param input
     * @param className
     */
    public static void logHandling(HandlerInput input, String className){
        logger.debug(className);
        logger.debug("State_Key: " + input.getAttributesManager().getSessionAttributes().get(Attributes.STATE_KEY));
        if(input.getRequestEnvelope().getRequest().getType().equals("IntentRequest")){ // Denn beim start ist es bspw ein "LaunchRequest" und hat dann kein Intent
            IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
            logger.debug("IntentName: " + intentRequest.getIntent().getName());
            if(intentRequest.getIntent().getSlots() != null) intentRequest.getIntent().getSlots().values().parallelStream().filter(item -> Objects.nonNull(item)).forEach(item -> logger.debug("Slots: " + item)); //Nullcheck, da im Intent keine Slots vorhanden sein können
        }
    }

    public static Optional<Response> generateUniversalOrExceptionResponse(HandlerInput input, LinkedHashMap<String, String> responseText, boolean shouldEndSession){ //String responseText[]
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();

        String currentState = (String) sessionAttributes.get(Attributes.STATE_KEY);
        return Arrays.stream(Attributes.STATES).filter(item -> item.equals(currentState)).findFirst().map(item -> buildResponse(input, responseText.get(item), shouldEndSession)).orElse(buildResponse(input, Constants.GRAMMAR_ERROR_MESSAGE, shouldEndSession));
    }

    public static Optional<Response> buildResponse(HandlerInput input, String responseMessage, boolean shouldEndSession){
        return input.getResponseBuilder()
                .withSpeech(responseMessage)
                .withShouldEndSession(shouldEndSession)
                .build();
    }
}
