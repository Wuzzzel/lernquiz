package main.java.lernquiz.model;

import java.util.*;

public class Constants {

    /**
     * Speech Synthesis Markup Language (SSML)
     */
    public static String SSML_MEDIUM_PAUSE = "<break strength=\"medium\"/>"; //Treat adjacent words as if separated by a single comma.
    public static String SSML_BREAK_SENTENCE = "<break strength=\"strong\"/>"; //Make a sentence break (equivalent to using the <s> tag)
    public static String SSML_BREAK_PARAGRAPH = "<break strength=\"x-strong\"/>"; //Make a paragraph break (equivalent to using the <p> tag)
    public static String SSML_EMPHASIS_STRONG = "<emphasis level=\"strong\">"; //Increase the volume and slow down the speaking rate so the speech is louder and slower.
    public static String SSML_EMPHASIS_REDUCED = "<emphasis level=\"reduced\">"; //Decrease the volume and speed up the speaking rate. The speech is softer and faster.
    public static String SSML_EMPHASIS_ENDTAG = "</emphasis>";
    public static String SSML_SAYAS_LETTERS = "<say-as interpret-as=\"characters\">"; // Spell out each letter
    public static String SSML_SAYAS_DATE = "<say-as interpret-as=\"date\" format=\"dmy\">"; //Indicate Date
    public static String SSML_SAYAS_ENDTAG = "</say-as>";

    /**
     * Initialisierung
     */
    public static String WELCOME_MESSAGE = "Herzlich Willkommen beim Lernquiz!";
    public static String FIRST_TIME_USER_MESSAGE = "Dieser Skill bietet dir die Möglichkeit ganz ohne Karteikarten zu lernen. " +
            "Dabei verwendet er aber eine ähnliche Logik, um dich wirklich schwere Fragen, öfter pauken zu lassen. Wir wünschen viel Spaß.";


    /**
     * Hauptmenü
     */
    public static String MAIN_MENU_NEWBIE_INITIAL_MESSAGE = "Was möchtest du tun? Ein Quiz beginnen oder dir Statistiken ausgeben lassen?";
    public static String MAIN_MENU_NEWBIE_MESSAGE = "Du bist zurück im Hauptmenü. Was möchtest du tun? Ein Quiz beginnen oder dir Statistiken ausgeben lassen?";
    public static String MAIN_MENU_NEWBIE_REPROMT_MESSAGE = "Entschuldigung, ich habe dich nicht gehört. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen. Für weitere Informationen sag Hilfe.";


    /**
     * Quiz
     */
    //Kategorie
    public static String QUIZ_CATEGORY_MESSAGE = "Du lernst nun Fragen zum Thema ";

    //Fragestellung
    public static String QUIZ_QUESTION_MESSAGE = "Die Frage lautet: ";
    public static String QUIZ_ANSWER_OPTIONS_MESSAGE = "Deine Antwortmöglichkeiten sind: ";
    public static String QUIZ_QUESTION_REPROMT_MESSAGE = "Entschuldigung, ich habe dich nicht gehört. Gib deine Antwort mit A, B oder C an. Für weitere Informationen sag Hilfe.";

    //Quizantwort
    public static String QUIZ_ANSWER_CORRECT_MESSAGE = "Deine Antwort war richtig.";
    public static String QUIZ_ANSWER_WRONG_MESSAGE = "Deine Antwort war falsch.";

    //Frageschwierigkeit
    public static String QUIZ_DIFFICULTY_MESSAGE = "Wie schwer fandest du diese Frage? Schwer, Mittelschwer oder einfach.";
    public static String QUIZ_DIFFICULTY_REPROMT_MESSAGE = "Entschuldigung, ich habe dich nicht gehört. Sag Schwer, Mittelschwer oder einfach. Für weitere Informationen sag Hilfe.";

    //Weitere Frage
    public static String QUIZ_ANOTHER_QUESTION_MESSAGE = "Noch eine Frage?";
    public static String QUIZ_ANOTHER_QUESTION_REPROMT_MESSAGE = "Entschuldigung, ich habe dich nicht gehört. Sag ja oder nein. Für weitere Informationen sag Hilfe.";

    //Hilfe um die schwere der Fragen zu Integers zu mappen
    public static HashMap<String, Integer> DIFFICULTY_INTEGER_MAP = new HashMap<String, Integer>() {{
        put("einfach", 0);
        put("leicht", 0);
        put("mittel", 1);
        put("mittelschwer", 1);
        put("schwer", 2);
    }};


    /**
     * Statistiken
     */
    public static String STATISTIC_QUESTION_NEWBIE_MESSAGE = "Die Statistischen Daten aus welchem Zeitraum möchtest du wissen? Sag beispielsweise Heute oder lass dir alle Daten Ausgeben.";
    public static String STATISTIC_QUESTION_REPROMT_MESSAGE = "Entschuldigung, ich habe dich nicht gehört. Gib einen Zeitraum wie heute an oder lass dir alle Daten Ausgeben. Für weitere Informationen sag Hilfe.";


    /**
     * Grammatikfehler
     */
    public static String GRAMMAR_ERROR_MESSAGE = "Es scheint ein Problem mit deinem Befehl zu geben. Bitte lies dir die Informationen zu diesem Skill, aus dem Alexa Skill Store durch und probiere es noch mal. Dieser Skill beendet sich jetzt.";

    public static LinkedHashMap<String, String> GRAMMAR_FIRST_ERROR_MESSAGE = new LinkedHashMap<String, String>() {{
        put(Attributes.START_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen.");
        put(Attributes.QUIZ_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an.");
        put(Attributes.DIFFICULTY_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach.");
        put(Attributes.ANOTHER_QUESTION_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein.");
        put(Attributes.STATISTIC_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib einen Zeitraum wie heute an oder lass dir alle Daten Ausgeben.");
    }};
    public static LinkedHashMap<String, String> GRAMMAR_SECOND_ERROR_MESSAGE = new LinkedHashMap<String, String>() {{
        put(Attributes.START_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen. Für weitere Informationen sag Hilfe.");
        put(Attributes.QUIZ_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an. Für weitere Informationen sag Hilfe.");
        put(Attributes.DIFFICULTY_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach. Für weitere Informationen sag Hilfe.");
        put(Attributes.ANOTHER_QUESTION_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein. Für weitere Informationen sag Hilfe.");
        put(Attributes.STATISTIC_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib einen Zeitraum wie heute an oder lass dir alle Daten Ausgeben. Für weitere Informationen sag Hilfe.");
    }};
    public static LinkedHashMap<String, String> GRAMMAR_THIRD_ERROR_MESSAGE = new LinkedHashMap<String, String>() {{
        put(Attributes.START_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen. Für weitere Informationen sag Hilfe.");
        put(Attributes.QUIZ_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an. Für weitere Informationen sag Hilfe.");
        put(Attributes.DIFFICULTY_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach. Für weitere Informationen sag Hilfe.");
        put(Attributes.ANOTHER_QUESTION_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein. Für weitere Informationen sag Hilfe.");
        put(Attributes.STATISTIC_STATE, "Entschuldigung, ich habe dich nicht Verstanden. Gib einen Zeitraum wie heute an oder lass dir alle Daten Ausgeben. Für weitere Informationen sag Hilfe.");
    }};
    public static ArrayList<LinkedHashMap<String, String>> GRAMMAR_ERROR = new ArrayList<LinkedHashMap<String, String>>() {{
        add(GRAMMAR_FIRST_ERROR_MESSAGE);
        add(GRAMMAR_SECOND_ERROR_MESSAGE);
        add(GRAMMAR_THIRD_ERROR_MESSAGE);
    }};


    /**
     * Exceptions
     */
    public static String EXCEPTION_ASK_SDK_MESSAGE = "Es ist ein Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_PERSISTENCE_MESSAGE = "Es ist ein Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_UNHANDLED_SKILL_MESSAGE = "Es ist ein Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_ANSWER_OUT_OF_BOUNDS_FIRST_MESSAGE = "Diese Frage hat nur ";
    public static String EXCEPTION_ANSWER_OUT_OF_BOUNDS_SECOND_MESSAGE = " Antwortmöglichkeiten. Bitte gib deine Antwort erneut an.";


    /**
     * Allgemeingültige Befehle
     */
    public static String UNIVERSAL_QUIT_MESSAGE = "Vielen Dank für die Nutzung und auf Wiedersehen.";
    public static String UNIVERSAL_QUIT_AND_SAFE_MESSAGE = "Dein aktueller Stand der Fragen wurde gespeichert. Vielen Dank für die Nutzung und auf Wiedersehen.";
    public static String UNIVERSAL_HELP_MAIN_MENU_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Hauptmenü. Von hier aus kannst du entweder ´Quiz starten´, oder ´Statistik starten´ sagen. Zu jederzeit kannst du außerdem ´Wiederholen´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_QUESTION_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und beantwortest die gestellte Frage. Beantworte sie mit A, B oder C. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_DIFFICULTY_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und beurteilst die Schwere der Frage. Beurteile sie mit Schwer, Mittelschwer oder einfach. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_ANOTHER_QUESTION_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und stehst davor eine neue Frage gestellt zu bekommen. Wenn du das möchtest antworte mit ja, ansonsten mit nein. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_STATISTIC_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich in den Statistiken. Hier kannst du einen Zeitraum, wie beispielweise „3 Tage“ oder Heute angeben und es werden dir die Statistiken dazu ausgegeben. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Verlassen´ sagen.";
    public static String UNIVERSAL_MAIN_MENU_MESSAGE = "Du befindest dich bereits im Hauptmenü.";

    public static LinkedHashMap<String, String> UNIVERSAL_QUIT = new LinkedHashMap<String, String>() {{
        put(Attributes.START_STATE, UNIVERSAL_QUIT_MESSAGE);
        put(Attributes.QUIZ_STATE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE);
        put(Attributes.DIFFICULTY_STATE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE);
        put(Attributes.ANOTHER_QUESTION_STATE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE);
        put(Attributes.STATISTIC_STATE, UNIVERSAL_QUIT_MESSAGE);
    }};
    public static LinkedHashMap<String, String> UNIVERSAL_HELP = new LinkedHashMap<String, String>() {{
        put(Attributes.START_STATE, UNIVERSAL_HELP_MAIN_MENU_MESSAGE);
        put(Attributes.QUIZ_STATE, UNIVERSAL_HELP_QUESTION_MESSAGE);
        put(Attributes.DIFFICULTY_STATE, UNIVERSAL_HELP_DIFFICULTY_MESSAGE);
        put(Attributes.ANOTHER_QUESTION_STATE, UNIVERSAL_HELP_ANOTHER_QUESTION_MESSAGE);
        put(Attributes.STATISTIC_STATE, UNIVERSAL_HELP_STATISTIC_MESSAGE);
    }};
}