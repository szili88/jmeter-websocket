package net.unit8.jmeter.protocol.websocket.control.gui;

import net.unit8.jmeter.protocol.websocket.sampler.WebSocketSampler;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * GUI for WebSocketSampler
 *
 * @author szili88
 */
public class WebSocketSamplerGui extends AbstractSamplerGui {
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
    private JTextField domain;
    private JTextField contentEncoding;
    private JSyntaxTextArea sendMessage;
    private JSyntaxTextArea recvMessage;

    public WebSocketSamplerGui() {
        LOGGER.warn("Initializing WebSocketSamplerGui...");
        init();
    }

    @Override
    public String getLabelResource() {
        throw new IllegalStateException("This shouldn't be called");
    }

    @Override
    public String getStaticLabel() {
        return "Websocket Sampler";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        domain.setText(element.getPropertyAsString(WebSocketSampler.SERVER_URI));
        contentEncoding.setText(element.getPropertyAsString(WebSocketSampler.CONTENT_ENCODING));
        sendMessage.setText(element.getPropertyAsString(WebSocketSampler.SEND_MESSAGE));
        recvMessage.setText(element.getPropertyAsString(WebSocketSampler.RECEIVE_MESSAGE));
    }

    @Override
    public TestElement createTestElement() {
        WebSocketSampler element = new WebSocketSampler();
        element.setName(getName());
        element.setProperty(WebSocketSampler.SERVER_URI, "ws://");
        element.setProperty(WebSocketSampler.CONTENT_ENCODING, "utf-8");
        configureTestElement(element);
        return element;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.setProperty(WebSocketSampler.SERVER_URI, domain.getText());
        element.setProperty(WebSocketSampler.CONTENT_ENCODING, contentEncoding.getText());
        element.setProperty(WebSocketSampler.SEND_MESSAGE, sendMessage.getText());
        element.setProperty(WebSocketSampler.RECEIVE_MESSAGE, recvMessage.getText());
        configureTestElement(element);
    }

    private JPanel getDomainPanel() {
        domain = new JTextField(20);
        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain"));
        label.setLabelFor(domain);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(domain, BorderLayout.CENTER);
        return panel;
    }

    protected Component getContentEncodingPanel() {
        contentEncoding = new JTextField(10);
        JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding"));
        contentEncodingLabel.setLabelFor(contentEncoding);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(contentEncodingLabel);
        panel.add(contentEncoding);
        panel.setMinimumSize(panel.getPreferredSize());
        return panel;
    }

    private JPanel getSendMessagePanel() {
        JLabel sendMessageLabel = new JLabel("Sent Message");
        sendMessage = new JSyntaxTextArea(5, 20);
        sendMessage.setLineWrap(true);
        sendMessageLabel.setLabelFor(sendMessage);
        JPanel sendMessagePanel = new JPanel(new BorderLayout(5, 0));
        sendMessagePanel.add(sendMessageLabel, BorderLayout.WEST);
        sendMessagePanel.add(new JTextScrollPane(sendMessage), BorderLayout.CENTER);
        return sendMessagePanel;
    }

    private JPanel getRecvMessagePanel() {
        JLabel recvMessageLabel = new JLabel("Received Message");
        recvMessage = new JSyntaxTextArea(5, 20);
        recvMessage.setLineWrap(true);
        recvMessageLabel.setLabelFor(recvMessage);
        JPanel recvMessagePanel = new JPanel(new BorderLayout(5, 0));
        recvMessagePanel.add(recvMessageLabel, BorderLayout.WEST);
        recvMessagePanel.add(new JTextScrollPane(recvMessage), BorderLayout.CENTER);
        return recvMessagePanel;
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));

        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        // MAIN PANEL
        VerticalPanel mainPanel = new VerticalPanel();
        JPanel webRequestPanel = new HorizontalPanel();
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
        serverPanel.add(getDomainPanel());

        webRequestPanel.add(serverPanel, BorderLayout.NORTH);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(getContentEncodingPanel());

        webRequestPanel.add(northPanel, BorderLayout.CENTER);

        mainPanel.add(webRequestPanel);
        mainPanel.add(getSendMessagePanel());
        mainPanel.add(getRecvMessagePanel());
        add(mainPanel, BorderLayout.CENTER);
    }
}
