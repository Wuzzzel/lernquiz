package main.java.lernquiz.dao.xmlModel;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "questions")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "category", "item" })
public class Questions {

    @XmlElement(name = "category")
    private String category;
    @XmlElement(name = "item")
    private List<QuizItem> item;

    public String getCategory() {
        return category;
    }

    public List<QuizItem> getItem() {
        return item;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setItem(List<QuizItem> item) {
        this.item = item;
    }
}
