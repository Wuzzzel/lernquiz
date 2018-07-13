package main.java.lernquiz.dao.xmlModel;

import javax.xml.bind.annotation.*;
import java.util.Map;

@XmlRootElement(name = "questions")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"category", "questionsMap"})
public class Questions {

    private String category;

    private Map<String, QuizItem> questionsMap; //Der Key repräsentiert die QuizfragenId

    public String getCategory() {
        return category;
    }

    public Map<String, QuizItem> getQuestionsMap() {
        return questionsMap;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuestionsMap(Map<String, QuizItem> questionsMap) {
        this.questionsMap = questionsMap;
    }
}
