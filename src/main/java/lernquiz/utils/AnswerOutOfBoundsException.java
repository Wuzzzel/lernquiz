package main.java.lernquiz.utils;

public class AnswerOutOfBoundsException extends RuntimeException{

    private int amountOfAnswers;

    public AnswerOutOfBoundsException(String message, int amountOfAnswers) {
        super(message);
        this.amountOfAnswers = amountOfAnswers;
    }

    public AnswerOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getAmountOfAnswers(){
        return this.amountOfAnswers;
    }
}
