package main.java.lernquiz.model;

public class Constants {

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
    public static String QUIZ_CATEGORY_MESSAGE = "Du lernst nun Fragen zum Thema Moderne Datenbanken. Viel Erfolg!";

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



    /**
     * Statistik
     */


    /**
     * Grammatikfehler
     */
    public static String GRAMMAR_ERROR_MESSAGE = "Es scheint ein Problem mit deinem Befehl zu geben. Bitte lies dir die Informationen zu diesem Skill, aus dem Alexa Skill Store durch und probiere es noch mal. Dieser Skill beendet sich jetzt.";
    public static String[] GRAMMAR_FIRST_ERROR_MESSAGE = {"Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen.",
            "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein."
    };
    public static String[] GRAMMAR_SECOND_ERROR_MESSAGE = {"Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein. Für weitere Informationen sag Hilfe."
    };
    public static String[] GRAMMAR_THIRD_ERROR_MESSAGE = {"Entschuldigung, ich habe dich nicht Verstanden. Du kannst ein Quiz starten, oder dir Statistiken ausgeben lassen. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Gib deine Antwort mit A, B oder C an. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag Schwer, Mittelschwer oder einfach. Für weitere Informationen sag Hilfe.",
            "Entschuldigung, ich habe dich nicht Verstanden. Sag ja oder nein. Für weitere Informationen sag Hilfe."
    };
    public static String[][] GRAMMAR_ERROR_MESSAGES = {GRAMMAR_FIRST_ERROR_MESSAGE, GRAMMAR_SECOND_ERROR_MESSAGE, GRAMMAR_THIRD_ERROR_MESSAGE};


    /**
     * Exceptions
     */
    public static String EXCEPTION_ASK_SDK_MESSAGE = "Es ist eine Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_PERSISTENCE_MESSAGE = "Es ist eine Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_UNHANDLED_SKILL_MESSAGE = "Es ist eine Fehler im Skill aufgetreten, daher beendet er sich nun.";
    public static String EXCEPTION_ANSWER_OUT_OF_BOUNDS_FIRST_MESSAGE = "Diese Frage hat nur ";
    public static String EXCEPTION_ANSWER_OUT_OF_BOUNDS_SECOND_MESSAGE = " Antwortmöglichkeiten. Bitte gib deine Antwort erneut an.";


    /**
     * Allgemeingültige Befehle
     */
    public static String UNIVERSAL_QUIT_MESSAGE = "Vielen Dank für die Nutzung und auf Wiedersehen.";
    public static String UNIVERSAL_QUIT_AND_SAFE_MESSAGE = "Dein aktueller Stand der Fragen wurde gespeichert. Vielen Dank für die Nutzung und auf Wiedersehen.";
    public static String[] UNIVERSAL_QUIT_MESSAGES = {UNIVERSAL_QUIT_MESSAGE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE, UNIVERSAL_QUIT_AND_SAFE_MESSAGE};
    public static String UNIVERSAL_HELP_MAIN_MENU_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Hauptmenü. Von hier aus kannst du entweder ´Quiz starten´, oder ´Statistik starten´ sagen. Zu jederzeit kannst du außerdem ´Wiederholen´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_QUESTION_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und beantwortest die gestellte Frage. Beantworte sie mit A, B oder C. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_DIFFICULTY_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und beurteilst die Schwere der Frage. Beurteile sie mit Schwer, Mittelschwer oder einfach. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String UNIVERSAL_HELP_ANOTHER_QUESTION_MESSAGE = "Okay, hier ein wenig Hilfe. Du befindest dich im Quiz und stehst davor eine neue Frage gestellt zu bekommen. Wenn du das möchtest antworte mit ja, ansonsten mit nein. Zu jederzeit kannst du außerdem ´Wiederholen´, ´Hauptmenü´ oder ´Beenden´ sagen.";
    public static String[] UNIVERSAL_HELP_MESSAGES = {UNIVERSAL_HELP_MAIN_MENU_MESSAGE, UNIVERSAL_HELP_QUESTION_MESSAGE, UNIVERSAL_HELP_DIFFICULTY_MESSAGE, UNIVERSAL_HELP_ANOTHER_QUESTION_MESSAGE};
    public static String UNIVERSAL_MAIN_MENU_MESSAGE = "Du befindest dich bereits im Hauptmenü.";

}
