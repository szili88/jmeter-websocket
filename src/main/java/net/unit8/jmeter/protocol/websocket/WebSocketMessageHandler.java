package net.unit8.jmeter.protocol.websocket;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

/**
 * WebSocketMessageHandler is responsible for sending and receiving messages over WebSocket.
 *
 * @author szili88
 */
@WebSocket
public class WebSocketMessageHandler {
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private Session session;
    private String responseMessage;

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOGGER.debug("Connection closed with status code " + statusCode + ", reason " + reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        LOGGER.debug("Successfully connected to " + session);
    }

    @OnWebSocketMessage
    public synchronized void onMessage(String message) {
        LOGGER.debug("Received message " + message);
        responseMessage = message;
        notify();
    }

    public void sendMessage(String message) throws IOException {
        session.getRemote().sendString(message);
    }

    public synchronized String receiveMessage(long timeout) throws InterruptedException {
        if (responseMessage == null) {
            wait(timeout);
        }
        return responseMessage;
    }
}