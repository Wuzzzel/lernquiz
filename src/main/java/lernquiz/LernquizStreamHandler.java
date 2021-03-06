package main.java.lernquiz;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import main.java.lernquiz.handlers.*;
import main.java.lernquiz.handlers.exception.AnswerOutOfBoundsHandler;
import main.java.lernquiz.handlers.exception.AskSdkExceptionHandler;
import main.java.lernquiz.handlers.exception.PersistenceExceptionHandler;
import main.java.lernquiz.handlers.exception.UnhandledSkillExceptionHandler;
import main.java.lernquiz.handlers.quiz.QuizAnotherQuestionIntentHandler;
import main.java.lernquiz.handlers.quiz.QuizAnswerIntentHandler;
import main.java.lernquiz.handlers.quiz.QuizDifficultyIntentHandler;
import main.java.lernquiz.handlers.quiz.QuizIntentHandler;
import main.java.lernquiz.handlers.statistic.StatisticIntentHandler;
import main.java.lernquiz.handlers.statistic.StatisticPeriodIntentHandler;
import main.java.lernquiz.handlers.universal.*;

public class LernquizStreamHandler extends SkillStreamHandler {

    /**
     * Diese Klasse stellt den benötigten Handler zum hosten des Skills als AWS Lambda Funktion zur verfügung
     */
    public LernquizStreamHandler() {
        super(getSkill());
    }

    /**
     * Erstellt ein Skill Objekt aus den definierten Handlern,
     * dass anschließend von den Alexa-Servern bei Verwendung des Skills angesprochen wird
     *
     * @return Skill bestehend aus den definierten Handlern
     */
    private static Skill getSkill() {
        return Skills.standard()
                .withTableName("lernquiz")
                .addRequestHandlers(
                        new LaunchRequestHandler(),
                        new QuizIntentHandler(),
                        new QuizAnswerIntentHandler(),
                        new QuizDifficultyIntentHandler(),
                        new QuizAnotherQuestionIntentHandler(),
                        new StatisticIntentHandler(),
                        new StatisticPeriodIntentHandler(),
                        new CancelandStopIntentHandler(),
                        new HelpIntentHandler(),
                        new RepeatIntentHandler(),
                        new UniversalMainMenuIntentHandler(),
                        new AssistModeIntentHandler(),
                        new SessionEndedRequestHandler())
                .addExceptionHandlers(
                        new PersistenceExceptionHandler(),
                        new UnhandledSkillExceptionHandler(),
                        new AnswerOutOfBoundsHandler(),
                        new AskSdkExceptionHandler())
                .build();
    }
}
