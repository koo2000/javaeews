package com.github.koo2000.sample.javaeews;

import javax.websocket.Session;

public class RemoteClient {
    private Session session;
    private String userId;

    public RemoteClient(Session session) {
        this.session = session;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Session getSession() {
        return session;
    }
}
