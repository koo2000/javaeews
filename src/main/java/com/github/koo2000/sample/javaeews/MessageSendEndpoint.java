package com.github.koo2000.sample.javaeews;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/messageSendEndpoint")
public class MessageSendEndpoint {

    @OnOpen
    public void open(Session session) {
        System.out.println("messageEndpoint @OnOpen: " + session.getId());
    }

    @OnMessage
    public void msg(String msg, Session session) {
        System.out.println("messageEndpoint @OnMessage:");
        System.out.println(msg);

        // TODO JSONを使えるようにする
        int pos = msg.indexOf(":");
        if (pos > 0) {
            String uid = msg.substring(0, pos);
            String message = msg.substring(pos + 1);
            PushEndpoint.sendMessage(uid, message);
        }
    }

    @OnClose
    public void close(CloseReason rsn, Session session) {
        System.out.println("messageEndpoint @OnClose: " + rsn);
    }

    @OnError
    public void error(Throwable t) {
        System.err.println("messageEndpoint @OnError:");
        t.printStackTrace(System.err);
    }
}