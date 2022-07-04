package server.ws;

import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import server.exception.ProtocolException;

public interface WsMessageProtocolHandler extends WsMessageHandler {

    @Override
    default void handleMessage(@NotNull WsMessageContext wsMessageContext) {
        try {
            handle(wsMessageContext);
        } catch (ProtocolException e) {
            wsMessageContext.send(String.format("ProtocolException %d", e.getCode()));
        }
    }

    void handle(@NotNull WsMessageContext wsMessageContext) throws ProtocolException;
}
