package main.java.lernquiz.dao.dynamoDbModel;

import java.util.HashMap;

public class UserData {

    private HashMap<String, IndividualQuestion> questions = new HashMap<>();

    private int assistMode = 0;

    public UserData(){}

    public HashMap<String, IndividualQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<String, IndividualQuestion> questions) {
        this.questions = questions;
    }

    public int getAssistMode() {
        return assistMode;
    }

    public void setAssistMode(int assistMode) {
        this.assistMode = assistMode;
    }
}