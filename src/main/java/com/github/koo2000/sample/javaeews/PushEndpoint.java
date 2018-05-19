package com.github.koo2000.sample.javaeeasync;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/pushEndpoint")
public class PushEndpoint {
    private static final Map<String, RemoteClient> ALL_SESSION = new HashMap<>();
    private static final Map<String, List<String>> USER_ID_SESSION_MAP = new HashMap<>();

    private static final String USER_ID_KEY = "USER_ID";

    private static synchronized void register(Session session) {
        ALL_SESSION.put(session.getId(), new RemoteClient(session));
        System.out.println("start session with session id:" + session.getId());
    }

    private static synchronized void unregister(Session session) {
        if (!ALL_SESSION.containsKey(session.getId())) {
            ALL_SESSION.remove(session.getId());
        }
    }

    private static synchronized void setUserId(Session session, String uid) {
        ALL_SESSION.get(session.getId()).setUserId(uid);
        if (!USER_ID_SESSION_MAP.containsKey(uid)) {
            USER_ID_SESSION_MAP.put(uid, new ArrayList<String>());
        }
        USER_ID_SESSION_MAP.get(uid).add(session.getId());
    }

    public static synchronized void sendMessage(String userId, String message) {
        List<String> sessionIds = USER_ID_SESSION_MAP.get(userId);

        if (sessionIds == null) {
            return;
        }
        for (String sessionId : sessionIds) {
            ALL_SESSION.get(sessionId).getSession().getAsyncRemote().sendText(message);
        }

    }

    @OnOpen
    public void open(Session session) {
        System.out.println("pushEndpoint @OnOpen: " + session.getId());
        register(session);
    }

    @OnMessage
    public void msg(String msg, Session session) {
        System.out.println("pushEndpoint @OnMessage:");
        System.out.println(msg);

        if (msg.startsWith("uid:")) {
            String userId = msg.substring(4);
            setUserId(session, userId);
        }
    }

    @OnClose
    public void close(CloseReason rsn, Session session) {
        System.out.println("pushEndpoint @OnClose: " + rsn);
        unregister(session);
    }

    @OnError
    public void error(Throwable t) {
        System.err.println("pushEndpoint @OnError:");
        t.printStackTrace(System.err);
    }
}
