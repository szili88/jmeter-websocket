package net.unit8.jmeter.protocol.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class WebSocketMessageHandler {
    private Session session;
    private String responseMessage;

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        System.out.printf("Got connect: %s%n", session);
    }

    @OnWebSocketMessage
    public synchronized void onMessage(String message) {
        System.out.println("Got msg: " + message);
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