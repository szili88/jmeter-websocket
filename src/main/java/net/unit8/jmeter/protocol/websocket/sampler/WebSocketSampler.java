package net.unit8.jmeter.protocol.websocket.sampler;

import net.unit8.jmeter.protocol.websocket.WebSocketMessageHandler;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.LongProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

/**
 * The sampler for WebSocket.
 *
 * @author szili88
 */
public class WebSocketSampler extends AbstractSampler implements TestStateListener {

    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
            Arrays.asList(new String[]{
                    "net.unit8.jmeter.protocol.websocket.control.gui.WebSocketSamplerGui",
                    "org.apache.jmeter.config.gui.SimpleConfigGui"}));

    private boolean initialized = false;
    public static final String SERVER_URI = "WebSocketSampler.serverURI";
    public static final String CONTENT_ENCODING = "WebSocketSampler.contentEncoding";
    public static final String SEND_MESSAGE = "WebSocketSampler.sendMessage";
    public static final String RECEIVE_MESSAGE = "WebSocketSampler.receiveMessage";
    public static final String RECEIVE_TIMEOUT = "WebSocketSampler.receiveTimeout";
    private Pattern receiveMessagePattern;
    private WebSocketMessageHandler websocketMessageHandler;
    private Session session;

    public WebSocketSampler() {
        LOGGER.warn("Initializing WebSocketSampler....");
    }

    public void initialize() throws Exception {
        receiveMessagePattern = (getReceivedMessage() != null) ? Pattern.compile(getReceivedMessage()) : null;
        websocketMessageHandler = new WebSocketMessageHandler();
        WebSocketClient webSocketClient = new WebSocketClient();
        Future<Session> futureSession = webSocketClient.connect(websocketMessageHandler, getUri());
        session = futureSession.get();
        initialized = true;
    }

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());

        if (!initialized) {
            try {
                initialize();
            } catch (Exception e) {
                result.setResponseMessage(e.getMessage());
                result.setSuccessful(false);
                return result;
            }
        }
        String sendMessage = getSendMessage();
        result.setSamplerData(sendMessage);
        result.sampleStart();
        result.setDataEncoding(getContentEncoding());
        try {
            websocketMessageHandler.sendMessage(sendMessage);
            String responseMessage = websocketMessageHandler.receiveMessage(getReceiveTimeout());
            if (responseMessage == null) {
                result.setResponseCode("204");
                throw new TimeoutException("No content (probably timeout).");
            } else if (receiveMessagePattern.matcher(responseMessage).matches()) {
                result.setResponseCodeOK();
                result.setResponseData(responseMessage, getContentEncoding());
                result.setSuccessful(true);
            } else {
                result.setResponseMessage("Invalid response data received: " + responseMessage);
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            result.setResponseMessage(e.getMessage());
            result.setSuccessful(false);
        }
        result.sampleEnd();

        return result;
    }


    @Override
    public void setName(String name) {
        if (name != null)
            setProperty(TestElement.NAME, name);
    }

    @Override
    public String getName() {
        return getPropertyAsString(TestElement.NAME);
    }

    @Override
    public void setComment(String comment) {
        setProperty(new StringProperty(TestElement.COMMENTS, comment));
    }

    @Override
    public String getComment() {
        return getProperty(TestElement.COMMENTS).getStringValue();
    }

    public URI getUri() throws URISyntaxException {
        String path = this.getServerURI();
        return new URI(path);
    }

    public void setServerURI(String value) {
        setProperty(SERVER_URI, value);
    }

    public String getServerURI() {
        return getPropertyAsString(SERVER_URI);
    }

    public void setContentEncoding(String charsetName) {
        setProperty(CONTENT_ENCODING, charsetName);
    }

    public String getContentEncoding() {
        return getPropertyAsString(CONTENT_ENCODING);
    }

    public void setSendMessage(String value) {
        setProperty(SEND_MESSAGE, value);
    }

    public String getSendMessage() {
        return getPropertyAsString(SEND_MESSAGE);
    }

    public void setReceivedMessage(String value) {
        setProperty(RECEIVE_MESSAGE, value);
    }

    public String getReceivedMessage() {
        return getPropertyAsString(RECEIVE_MESSAGE);
    }

    public void setReceiveTimeout(long value) {
        setProperty(new LongProperty(RECEIVE_TIMEOUT, value));
    }

    public long getReceiveTimeout() {
        return getPropertyAsLong(RECEIVE_TIMEOUT, 20000L);
    }

    @Override
    public void testStarted() {
        testStarted("");
    }

    @Override
    public void testStarted(String host) {
    }

    @Override
    public void testEnded() {
        testEnded("");
    }

    @Override
    public void testEnded(String host) {
        try {
            session.close();
        } catch (Exception e) {
            LOGGER.error("Exception while closing WebSocket session.", e);
        }
    }

    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
    }
}
