package main.java.lernquiz.dao.dynamoDbModel;

public class Entry {

    private int questionDifficulty;
    private boolean correctAnswered;

    public Entry(){}

    public Entry(int questionDifficulty, boolean correctAnswered){
        this.questionDifficulty = questionDifficulty;
        this.correctAnswered = correctAnswered;
    }

    public int getQuestionDifficulty() {
        return questionDifficulty;
    }

    public boolean isCorrectAnswered() {
        return correctAnswered;
    }

    public void setQuestionDifficulty(int questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public void setCorrectAnswered(boolean correctAnswered) {
        this.correctAnswered = correctAnswered;
    }
}
