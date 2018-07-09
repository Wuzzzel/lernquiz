package main.java.lernquiz;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.attributes.persistence.impl.DynamoDbPersistenceAdapter;
import com.amazonaws.services.dynamodbv2.AbstractAmazonDynamoDB;
import main.java.lernquiz.handlers.*;
import main.java.lernquiz.handlers.exception.AnswerOutOfBoundsHandler;
import main.java.lernquiz.handlers.exception.AskSdkExceptionHandler;
import main.java.lernquiz.handlers.exception.PersistenceExceptionHandler;
import main.java.lernquiz.handlers.exception.UnhandledSkillExceptionHandler;
import main.java.lernquiz.handlers.quiz.QuizAnotherQuestionHandler;
import main.java.lernquiz.handlers.quiz.QuizAnswerHandler;
import main.java.lernquiz.handlers.quiz.QuizDifficultyHandler;
import main.java.lernquiz.handlers.quiz.QuizIntentHandler;
import main.java.lernquiz.handlers.statistic.StatisticIntentHandler;
import main.java.lernquiz.handlers.statistic.StatisticPeriodIntentHandler;
import main.java.lernquiz.handlers.universal.CancelandStopIntentHandler;
import main.java.lernquiz.handlers.universal.HelpIntentHandler;
import main.java.lernquiz.handlers.universal.RepeatIntentHandler;
import main.java.lernquiz.handlers.universal.UniversalMainMenuHandler;

public class LernquizStreamHandler extends SkillStreamHandler {

    /**
     *
     * @return
     */
    private static Skill getSkill() {
        return Skills.standard()
                .withTableName("lernquiz")
                .addRequestHandlers(
                        new LaunchRequestHandler(),
                        new QuizIntentHandler(),
                        new QuizAnswerHandler(),
                        new QuizDifficultyHandler(),
                        new QuizAnotherQuestionHandler(),
                        new StatisticIntentHandler(),
                        new StatisticPeriodIntentHandler(),
                        new CancelandStopIntentHandler(),
                        new HelpIntentHandler(),
                        new RepeatIntentHandler(),
                        new UniversalMainMenuHandler(),
                        new SessionEndedRequestHandler())
                .addExceptionHandlers(
                        new PersistenceExceptionHandler(),
                        new UnhandledSkillExceptionHandler(),
                        new AnswerOutOfBoundsHandler(),
                        new AskSdkExceptionHandler())
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    /**
     *
     */
    public LernquizStreamHandler() {
        super(getSkill());
    }

}
