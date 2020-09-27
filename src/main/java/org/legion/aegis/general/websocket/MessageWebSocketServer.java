package org.legion.aegis.general.websocket;

import org.legion.aegis.admin.service.PortalLoginService;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/issue/{userId}/{token}")
public class MessageWebSocketServer {


    private static final Logger log = LoggerFactory.getLogger(MessageWebSocketServer.class);
    private static final ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("token") String token) throws Exception {
        PortalLoginService service = SpringUtils.getBean(PortalLoginService.class);
        String correctToken = service.getUserToken(StringUtils.parseIfIsLong(userId));
        if (StringUtils.isNotBlank(correctToken) && correctToken.equals(token)) {
            sessionMap.put(Long.parseLong(userId), session);
            log.info("Web socket opened for user [" + userId + "]");
        } else {
            log.warn("Invalid Websocket Connection Request");
            log.warn("Invalid Token -> [" + token + "]");
            session.close();
        }

    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessionMap.remove(Long.parseLong(userId));
        log.info("Web socket closed");
    }

    public void sendMessage(Long userId, String message) throws Exception {
        Session userSocket = sessionMap.get(userId);
        if (userSocket != null) {
            userSocket.getBasicRemote().sendText(message);
        }
    }
}
