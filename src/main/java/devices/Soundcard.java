package devices;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

public class Soundcard {
	WebSocketSession session;
	
	public Soundcard(WebSocketSession session) {
		this.session = session;
	}
	
	public void playSound() {
		String sound = "sound!";
		try {
			String jsonSound = new Gson().toJson(new Sound(sound));
			session.sendMessage(new TextMessage(jsonSound));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Sound {
		@SuppressWarnings("unused")
		String type = "Sound";
		@SuppressWarnings("unused")
		String sound;
		
		Sound(String sound) {
			this.sound = sound;
		}
	}
}
