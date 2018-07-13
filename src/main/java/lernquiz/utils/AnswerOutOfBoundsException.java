package main.java.lernquiz.utils;

public class AnswerOutOfBoundsException extends RuntimeException {

    private int amountOfAnswers;

    /**
     * Erzeugt eine Excpetion die geworfen werden kann, wenn die Nutzerangaben auserhalb des
     * vom Skill vorgesehenen Eingabebereiches liegen. Mit übergeben wird eine detailierte Fehlernachricht,
     * sowie die größe des vorgesehenen Eingabebereiches
     *
     * @param message         detailierte Fehlernachricht
     * @param amountOfAnswers größe des vorgesehenen Eingabebereiches
     */
    public AnswerOutOfBoundsException(String message, int amountOfAnswers) {
        super(message);
        this.amountOfAnswers = amountOfAnswers;
    }

    /**
     * Getter für das Attribut amountOfAnswers
     *
     * @return amountOfAnswers {@link int}
     */
    public int getAmountOfAnswers() {
        return this.amountOfAnswers;
    }
}
