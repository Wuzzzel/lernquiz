package main.java.lernquiz.model;

public class Attributes {

    /**
     * Zustände
     * Werden neben den Intents, zur bestimmung des nächsten RequestHandlers genutzt
     */
    public static final String START_STATE = "_start";
    public static final String QUIZ_STATE = "_quiz";
    public static final String DIFFICULTY_STATE = "_difficulty";
    public static final String ANOTHER_QUESTION_STATE = "_anotherQuestion";
    public static final String STATISTIC_STATE = "_statistic";


    /**
     * Schlüssel
     * Werden als Key genutzt, um Daten in der Session-Map abzuspeichern
     */
    public static final String STATE_KEY = "state";
    public static final String FIRST_QUESTION_KEY = "first";
    public static final String RESPONSE_KEY = "response";
    public static final String QUIZ_ITEM_KEY = "item";
    public static final String GRAMMAR_EXCEPTIONS_COUNT_KEY = "grammar";
    public static final String QUESTION_CORRECT_KEY = "correct";
    public static final String LAST_QUIZ_ITEM_KEY = "lastItem";
    public static final String ASSIST_MODE = "assistMode";
}