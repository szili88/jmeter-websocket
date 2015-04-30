package net.unit8.jmeter.protocol.websocket.control.gui;

import net.unit8.jmeter.protocol.websocket.sampler.WebSocketSampler;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * The GUI for WebSocketSampler.
 *
 * @author szili88
 */
public class WebSocketSamplerGui extends AbstractSamplerGui {
    private JTextField serverURI;
    private JTextField contentEncoding;
    private JTextField receiveTimeout;
    private JSyntaxTextArea sendMessage;
    private JSyntaxTextArea expectMessage;

    public WebSocketSamplerGui() {
        init();
    }

    @Override
    public String getLabelResource() {
        throw new IllegalStateException("This shouldn't be called");
    }

    @Override
    public String getStaticLabel() {
        return "WebSocket Sampler";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        serverURI.setText(element.getPropertyAsString(WebSocketSampler.SERVER_URI));
        contentEncoding.setText(element.getPropertyAsString(WebSocketSampler.CONTENT_ENCODING));
        receiveTimeout.setText(element.getPropertyAsString(WebSocketSampler.RECEIVE_TIMEOUT));
        sendMessage.setText(element.getPropertyAsString(WebSocketSampler.SEND_MESSAGE));
        expectMessage.setText(element.getPropertyAsString(WebSocketSampler.EXPECT_MESSAGE));
    }

    @Override
    public TestElement createTestElement() {
        WebSocketSampler element = new WebSocketSampler();
        element.setName(getName());
        element.setProperty(WebSocketSampler.SERVER_URI, "ws://");
        element.setProperty(WebSocketSampler.CONTENT_ENCODING, "utf-8");
        element.setProperty(WebSocketSampler.RECEIVE_TIMEOUT, 5000);
        configureTestElement(element);
        return element;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.setProperty(WebSocketSampler.SERVER_URI, serverURI.getText());
        element.setProperty(WebSocketSampler.CONTENT_ENCODING, contentEncoding.getText());
        element.setProperty(WebSocketSampler.RECEIVE_TIMEOUT, Integer.parseInt(receiveTimeout.getText()));
        element.setProperty(WebSocketSampler.SEND_MESSAGE, sendMessage.getText());
        element.setProperty(WebSocketSampler.EXPECT_MESSAGE, expectMessage.getText());
        configureTestElement(element);
    }

    private JPanel getConfigurationPanel() {
        serverURI = new JTextField(20);
        JLabel serverURILabel = new JLabel("Server URI");
        serverURILabel.setLabelFor(serverURI);
        contentEncoding = new JTextField(6);
        JLabel contentEncodingLabel = new JLabel("Content Encoding");
        contentEncodingLabel.setLabelFor(contentEncoding);
        receiveTimeout = new JTextField(6);
        JLabel receiveTimeoutLabel = new JLabel("Receive Timeout");
        receiveTimeoutLabel.setLabelFor(receiveTimeout);

        JPanel configurationPanel = new HorizontalPanel();
        configurationPanel.add(serverURILabel);
        configurationPanel.add(serverURI);
        configurationPanel.add(Box.createHorizontalStrut(5));
        configurationPanel.add(contentEncodingLabel);
        configurationPanel.add(contentEncoding);
        configurationPanel.add(receiveTimeoutLabel);
        configurationPanel.add(receiveTimeout);
        return configurationPanel;
    }

    private JPanel getSendMessagePanel() {
        JLabel sendMessageLabel = new JLabel("Sent Message");
        sendMessage = new JSyntaxTextArea(5, 20);
        sendMessage.setLineWrap(true);
        sendMessageLabel.setLabelFor(sendMessage);
        JPanel sendMessagePanel = new VerticalPanel();
        sendMessagePanel.add(sendMessageLabel);
        sendMessagePanel.add(new JTextScrollPane(sendMessage));
        return sendMessagePanel;
    }

    private JPanel getExpectMessagePanel() {
        JLabel expectMessageLabel = new JLabel("Received Message");
        expectMessage = new JSyntaxTextArea(5, 20);
        expectMessage.setLineWrap(true);
        expectMessageLabel.setLabelFor(expectMessage);
        JPanel expectMessagePanel = new VerticalPanel();
        expectMessagePanel.add(expectMessageLabel);
        expectMessagePanel.add(new JTextScrollPane(expectMessage));
        return expectMessagePanel;
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));

        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(getConfigurationPanel());
        mainPanel.add(getSendMessagePanel());
        mainPanel.add(getExpectMessagePanel());
        add(mainPanel, BorderLayout.CENTER);
    }
}
