package com.owenobyrne.alexa.findmyiphone;

import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;

import icloud.Sosumi;
import icloud.SosumiException;

/**
 * Great instructions on how to create this:
 * https://github.com/amzn/alexa-skills-kit-java/tree/master/samples/src/main/java/savvyconsumer
 * 
 * mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
 * 
 */

public class FindMyIphoneSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(FindMyIphoneSpeechlet.class);
    private static String DECRYPTED_ICLOUD_USERNAME = decryptKey("icloud_username");
    private static String DECRYPTED_ICLOUD_PASSWORD = decryptKey("icloud_password");
    
    private Sosumi mSosumi = null;
    
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}, icloud user = {}", request.getRequestId(),
                session.getSessionId(), DECRYPTED_ICLOUD_USERNAME);

        try {
			mSosumi = new Sosumi("https://fmipmobile.icloud.com", DECRYPTED_ICLOUD_USERNAME, DECRYPTED_ICLOUD_PASSWORD);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        OutputSpeech outputSpeech = new SsmlOutputSpeech();
        ((SsmlOutputSpeech) outputSpeech).setSsml("<speak>I can help you find your iPhone - say <break time=\"0.3s\"/> ask my iphone to ring.</speak>");

        return SpeechletResponse.newTellResponse(outputSpeech);

    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("FindIphone".equals(intentName)) {
            return findIphone();
       
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // any cleanup logic goes here
    }


    /**
     * Fetches the top ten selling titles from the Product Advertising API.
     *
     * @throws SpeechletException
     */
    private SpeechletResponse findIphone() throws SpeechletException {
    	
    	if (mSosumi != null) {
	        // Owen's has a real ’ not a single quote '.
    		// Niamh's iPhone 6s
	        try {
				//mSosumi.sendMessage("Owen’s MacBook Pro", "Hey there", "Found your phone. XXX", true);
				mSosumi.sendMessage("Niamh's iPhone 6s", "Hey there", "Found your phone. xxx", true);
			} catch (SosumiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Attempting to find your iPhone now.");

        return SpeechletResponse.newTellResponse(speech);
    	
    }

    
    private static String decryptKey(String key) {
        System.out.println("Decrypting key");
        byte[] encryptedKey = Base64.decode(System.getenv(key));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }

}