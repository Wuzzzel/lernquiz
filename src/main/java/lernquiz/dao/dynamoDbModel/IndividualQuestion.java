package main.java.lernquiz.dao.dynamoDbModel;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;

public class IndividualQuestion {

    private int lastQuestionDifficulty;
    private HashMap<String, Entry> entries; // Der String repräsentiert hier den Typ Datetime, also wann die frage beantwortet wurde

    public IndividualQuestion(){}

    public int getLastQuestionDifficulty() {
        return lastQuestionDifficulty;
    }

    public HashMap<String, Entry> getEntries() {
        return entries;
    }

    public void setLastQuestionDifficulty(int lastQuestionDifficulty) {
        this.lastQuestionDifficulty = lastQuestionDifficulty;
    }

    public void setEntries(HashMap<String, Entry> entries) {
        this.entries = entries;
    }
}
