package main.java.lernquiz.dao.dynamoDbModel;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;

public class IndividualQuestion {

    private int lastQuestionDifficulty;
    private HashMap<String, Entry> entrys; // Der String repräsentiert hier den Typ Datetime, also wann die frage beantwortet wurde

    public IndividualQuestion(){}

    public int getLastQuestionDifficulty() {
        return lastQuestionDifficulty;
    }

    public HashMap<String, Entry> getEntrys() {
        return entrys;
    }

    public void setLastQuestionDifficulty(int lastQuestionDifficulty) {
        this.lastQuestionDifficulty = lastQuestionDifficulty;
    }

    public void setEntrys(HashMap<String, Entry> entrys) {
        this.entrys = entrys;
    }
}
