package main.java.lernquiz.handlers.quiz;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.exception.PersistenceException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.lernquiz.model.Attributes;
import main.java.lernquiz.model.Constants;
import main.java.lernquiz.dao.xmlModel.QuizItem;
import main.java.lernquiz.utils.AnswerOutOfBoundsException;
import main.java.lernquiz.utils.QuestionUtils;

import java.util.*;
import java.util.stream.IntStream;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.sessionAttribute;

public class QuizAnswerIntentHandler implements RequestHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Wird vom SDK aufgerufen, um zu bestimmen, ob dieser Handler in der Lage ist die aktuelle Anfrage zu bearbeiten.
     * Gibt true zurück, wenn der Handler die aktuelle Anfrage bearbeiten kann, ansonsten false
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return true, wenn der Handler die aktuelle Anfrage bearbeiten kann
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizAnswerIntent").and(sessionAttribute(Attributes.STATE_KEY, Attributes.QUIZ_STATE)));
    }

    /**
     * Wird vom SDK aufgerufen, wenn dieser Antwort-Handler genutzt wird.
     * Akzeptiert ein HandlerInput und generiert eine optionale Antwort. Verarbeitet die Nutzerangaben bezüglich der Quizfragenantwort
     * und fragt die Nutzer wie schwer sie die Quizfrage fanden
     *
     * @param input Wrapper, der die aktuelle Anfrage, den Kontext und den Zustand beinhaltet
     * @return eine optionale Antwort {@link Response} vom Handler
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        //Daten aus Session holen. Log-Handling einrichten
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        int assistMode = (int) sessionAttributes.get(Attributes.ASSIST_MODE);
        QuestionUtils.logHandling(input, this.getClass().getName());

        //Quizfrage aus der Session lesen
        Map<String, String> quizItemMap = (LinkedHashMap<String, String>) sessionAttributes.get(Attributes.QUIZ_ITEM_KEY); //Es wird von JSON zu Map gewandelt
        QuizItem quizItem = MAPPER.convertValue(quizItemMap, QuizItem.class); //Muss dann mit dem Mapper in das ursprüngliche Objekt konvertiert werden

        //Nutzerantwort aus Slot holen und je nach Korrektheit Antworttext wählen
        String responseText;
        int correctAnswerPosition = getCorrectAnswer(quizItem);
        boolean correct = compareSlots(intentRequest.getIntent().getSlots(), quizItem.getAnswers().size(), correctAnswerPosition);
        if (correct) {
            responseText = Constants.QUIZ_ANSWER_CORRECT_MESSAGE;
        } else {
            String correctAnswer = quizItem.getAnswers().get(correctAnswerPosition - 1);
            responseText = Constants.QUIZ_ANSWER_WRONG_MESSAGE + " " + correctAnswer + "." + Constants.SSML_BREAK_PARAGRAPH;
        }

        //Antwort-Korrektheit in die Session schreiben, damit sie nach der Abfrage der Schwierigkeit vorhanden sind, um sie dort zusammen in die Datenbank zu schreiben
        sessionAttributes.put(Attributes.QUESTION_CORRECT_KEY, correct);
        //Antwort generieren, Intentdaten in Session speichern und return
        sessionAttributes.put(Attributes.STATE_KEY, Attributes.DIFFICULTY_STATE);
        sessionAttributes.put(Attributes.GRAMMAR_EXCEPTIONS_COUNT_KEY, 0);
        responseText += " " + Constants.QUIZ_DIFFICULTY_MESSAGE[assistMode];
        sessionAttributes.put(Attributes.RESPONSE_KEY, responseText);
        return input.getResponseBuilder()
                .withSpeech(responseText)
                .withReprompt(Constants.QUIZ_DIFFICULTY_REPROMT_MESSAGE[assistMode])
                .withShouldEndSession(false)
                .build();
    }

    /**
     * Vergleicht den Inhalt der übergebenen Slots auf Nutzerantworten und gibt bei einer korrekten Antwort true, ansonsten false zurück
     *
     * @param slots           beinhaltet Slots der Nuterantwort
     * @param amountOfAnswers gibt an wie viele Antwortmöglichkeiten die Quizfrage besitzt
     * @param correctAnswer   gibt die korrekte Antwortmöglichkeit an
     * @return true, wenn Nutzerantwort korrekt, ansonsten false {@link boolean}
     * @throws AskSdkException,            wenn der Inhalt eines Slots "?" gleicht
     * @throws AnswerOutOfBoundsException, wenn die Antwort des Nutzers größer als amountOfAnswers ist
     */
    private boolean compareSlots(Map<String, Slot> slots, int amountOfAnswers, int correctAnswer)
            throws AskSdkException, AnswerOutOfBoundsException {
        for (Slot slot : slots.values()) {
            if (slot.getValue() != null) {
                int answerNumber = -1;
                //Ein ? im slotvalue kann vorkommen, wenn die Nutzer beispielsweise mit "Antwort ein" antworten
                if (slot.getValue().equals("?"))
                    throw new AskSdkException("Antwort des Nutzers konnte nicht erkannt werden.");
                if (slot.getName().equals("number"))
                    answerNumber = Integer.valueOf(slot.getValue()); //Antwort in Integer wandeln
                if (slot.getName().equals("letter")) answerNumber = letterToInt(slot.getValue());
                if (answerNumber == correctAnswer) return true;
                //Es gibt beispielsweise nur 3 Antwortmöglichkeiten, die Nutzer sagen aber "Antwort 4":
                if (answerNumber > amountOfAnswers)
                    throw new AnswerOutOfBoundsException(
                            "Antwort des Nutzers ist nicht in der Fragestellung enthalten.", amountOfAnswers);
            }
        }
        return false;
    }

    /**
     * Konvertiert den ersten Buchstaben eines Strings in die dazugehörige Integer Zahl (a->1, b->2, usw.)
     *
     * @param letter der zu konvertierende Buchstabe
     * @return Integer-Zahl des Buchstabens {@link int}
     */
    public int letterToInt(String letter) {
        return letter.toLowerCase().charAt(0) - 'a' + 1;
    }

    /**
     * Findet in dem übergebenen QuizItem die Position der korrekten Antwortmöglichkeit und gibt diese zurück
     *
     * @param quizItem in dem nach der korrekten Antwortmöglichkeit gesucht werden soll
     * @return Position der korrekten Antwortmöglichkeit {@link int}
     */
    public int getCorrectAnswer(QuizItem quizItem) {
        List<Boolean> correctAnswers = quizItem.getCorrectAnswers();
        return IntStream.range(0, correctAnswers.size()).filter(i -> correctAnswers.get(i) == true)
                .findFirst().orElseThrow(() -> new PersistenceException("Es wurde keine richtige Antwort gefunden. Fehler in der XML Datei")) + 1;
    }
}
