package net.unit8.jmeter.protocol.websocket.sampler;

import net.unit8.jmeter.protocol.websocket.WebSocketMessageHandler;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

/**
 * The sampler for WebSocketSampler.
 *
 * @author szili88
 */
public class WebSocketSampler extends AbstractSampler implements TestStateListener {

    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private static final Set<String> APPLICABLE_CONFIG_CLASSES = new HashSet<String>(
            Arrays.asList(new String[]{
                    "net.unit8.jmeter.protocol.websocket.control.gui.WebSocketSamplerGui",
                    "org.apache.jmeter.config.gui.SimpleConfigGui"}));

    public static final String SERVER_URI = "WebSocketSampler.serverURI";
    public static final String CONTENT_ENCODING = "WebSocketSampler.contentEncoding";
    public static final String SEND_MESSAGE = "WebSocketSampler.sendMessage";
    public static final String EXPECT_MESSAGE = "WebSocketSampler.expectMessage";
    public static final String RECEIVE_TIMEOUT = "WebSocketSampler.receiveTimeout";
    private WebSocketMessageHandler websocketMessageHandler;
    private Pattern expectedMessagePattern;
    private WebSocketClient webSocketClient;

    private void init() throws Exception {
        expectedMessagePattern = (getPropertyAsString(EXPECT_MESSAGE) != null) ? Pattern.compile(getPropertyAsString(EXPECT_MESSAGE)) : null;
        websocketMessageHandler = new WebSocketMessageHandler();
        webSocketClient = new WebSocketClient();
        webSocketClient.start();
        webSocketClient.connect(websocketMessageHandler, new URI(getPropertyAsString(SERVER_URI))).get();
    }

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        String sendMessage = getPropertyAsString(SEND_MESSAGE);
        result.setSamplerData(sendMessage);
        result.setDataEncoding(getPropertyAsString(CONTENT_ENCODING));
        try {
            init();
        } catch (Exception e) {
            LOGGER.error("Failed to initialized WebSocket connection.", e);
            result.setResponseMessage(e.getMessage());
            result.setSuccessful(false);
            return result;
        }
        try {
            result.sampleStart();
            websocketMessageHandler.sendMessage(sendMessage);
            String responseMessage = websocketMessageHandler.receiveMessage(getPropertyAsInt(RECEIVE_TIMEOUT));
            if (responseMessage == null) {
                result.setResponseCode("204");
                throw new TimeoutException("No content (probably timeout).");
            } else if (expectedMessagePattern.matcher(responseMessage).matches()) {
                result.setResponseCodeOK();
                result.setResponseData(responseMessage, getPropertyAsString(CONTENT_ENCODING));
                result.setSuccessful(true);
            } else {
                result.setResponseMessage("Invalid response data received: " + responseMessage);
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            result.setResponseMessage(e.getMessage());
            result.setSuccessful(false);
            LOGGER.error("Failed to execute WebSocket sampler.", e);
        } finally {
            result.sampleEnd();
        }

        return result;
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
            webSocketClient.stop();
        } catch (Exception e) {
            LOGGER.error("Exception while closing WebSocket connection.", e);
        }
    }

    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLICABLE_CONFIG_CLASSES.contains(guiClass);
    }
}
