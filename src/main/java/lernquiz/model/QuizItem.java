package main.java.lernquiz.model;

import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class QuizItem {

    private String question;
    private LinkedHashMap<String, Boolean> answers; //TreeMap damit Antworten sortiert sind/ Muss das überhaupt so sein? Man shuffelt die Antworten ja eh durch...

    public QuizItem(){}

    public QuizItem(String question, LinkedHashMap<String, Boolean> answers){
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion(){
        return this.question;
    }

    public void setQuestion(String question){
        this.question = question;
    }

    public LinkedHashMap<String, Boolean> getAnswers(){
        return this.answers;
    }

    public void setAnswers(LinkedHashMap<String, Boolean> answers){
        this.answers = answers;
    }
}
