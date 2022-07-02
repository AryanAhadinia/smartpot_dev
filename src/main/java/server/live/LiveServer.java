package server.live;

import io.javalin.websocket.WsContext;

import java.util.HashMap;
import java.util.HashSet;

public class LiveServer {
    private final HashMap<Integer, HashSet<WsContext>> broadcastSubscribers;

    public LiveServer() {
        this.broadcastSubscribers = new HashMap<>();
    }

    public void subscribe(WsContext ws, int broadcastId) {
        if (broadcastSubscribers.containsKey(broadcastId)) {
            broadcastSubscribers.get(broadcastId).add(ws);
        } else {
            HashSet<WsContext> subscribers = new HashSet<>();
            subscribers.add(ws);
            broadcastSubscribers.put(broadcastId, subscribers);
        }
    }

    public void unsubscribe(WsContext ws, int broadcastId) {
        broadcastSubscribers.get(broadcastId).remove(ws);
    }

    public void broadcast(String message, int broadcastId) {
        if (broadcastSubscribers.containsKey(broadcastId)) {
            for (WsContext wsContext : broadcastSubscribers.get(broadcastId)) {
                if (wsContext.session.isOpen()) {
                    wsContext.send(message);
                }
            }
        }
    }

    public void terminateBroadcastId(String terminationMessage, int broadcastId) {
        broadcast(terminationMessage, broadcastId);
        for (WsContext wsContext : broadcastSubscribers.get(broadcastId)) {
            unsubscribe(wsContext, broadcastId);
        }
    }

    public void terminateWebSocket(WsContext ws) {

    }
}
