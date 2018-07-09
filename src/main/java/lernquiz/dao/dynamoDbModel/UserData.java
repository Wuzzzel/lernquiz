package main.java.lernquiz.dao.dynamoDbModel;

import java.util.HashMap;

public class UserData {

    private HashMap<String, IndividualQuestion> questions;

    public UserData(){}

    public HashMap<String, IndividualQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<String, IndividualQuestion> questions) {
        this.questions = questions;
    }
}