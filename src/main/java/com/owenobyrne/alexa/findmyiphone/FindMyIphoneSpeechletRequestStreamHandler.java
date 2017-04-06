package com.owenobyrne.alexa.findmyiphone;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * This class is created by the Lambda environment when a request comes in. All calls will be
 * dispatched to the Speechlet passed into the super constructor.
 */
public final class FindMyIphoneSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.578c766a-f9c9-49eb-8c4a-c2da3c804e75");
    }

    public FindMyIphoneSpeechletRequestStreamHandler() {
        super(new FindMyIphoneSpeechlet(), supportedApplicationIds);
    }
}

