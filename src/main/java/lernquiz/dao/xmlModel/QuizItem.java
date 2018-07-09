package main.java.lernquiz.dao.xmlModel;


import javax.xml.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "question", "answers", "correctAnswers" })
public class QuizItem {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "question")
    private String question;

    @XmlElementWrapper(name = "answers")
    @XmlElement(name = "answer")
    private List<String> answers;

    @XmlElementWrapper(name = "correctAnswers")
    @XmlElement(name = "answer")
    private List<Boolean> correctAnswers;

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<Boolean> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setCorrectAnswer(List<Boolean> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public List<String> getAnswersWithIsolator(){ //Vll hier raus nehmen und woanders hin schieben, weil das zeug sonst im json output auftaucht
        char answerLetter = 'A';
        List<String> answers = this.answers;
        for(int i = 0; i < answers.size(); i++){
            answers.set(i, "Antwort " + answerLetter + ": " + answers.get(i));
            answerLetter++;
        }
        return answers;
    }
}
