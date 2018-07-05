package main.java.lernquiz;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import main.java.lernquiz.handlers.CancelandStopIntentHandler;
import main.java.lernquiz.handlers.HelloWorldIntentHandler;
import main.java.lernquiz.handlers.HelpIntentHandler;
import main.java.lernquiz.handlers.SessionEndedRequestHandler;
import main.java.lernquiz.handlers.LaunchRequestHandler;

public class HelloWorldStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new HelloWorldIntentHandler(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new SessionEndedRequestHandler())
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    public HelloWorldStreamHandler() {
        super(getSkill());
    }

}
