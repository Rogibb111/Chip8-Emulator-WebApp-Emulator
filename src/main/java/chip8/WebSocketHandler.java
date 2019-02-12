package chip8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import computer.Computer;

import static computer.Computer.*;


@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	Map<WebSocketSession, String> sessions = new HashMap<WebSocketSession, String>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		Map<?, ?> value = new Gson().fromJson(message.getPayload(), Map.class);
		String id = value.get("id").toString();
		Computer computer = getInstance(id);
		
		switch(value.get("type").toString()) {
			case "init":
				sessions.put(session, id);
				computer.start(session);
				break;
			case "keyPress":
				computer.keyboard.updateKeyboard((ArrayList<?>) value.get("keyboard"));
				break;
			default:
		}
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
		String id = sessions.get(session);
		removeInstance(id);
		sessions.remove(session);
	}
}