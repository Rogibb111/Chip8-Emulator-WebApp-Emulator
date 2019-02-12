package devices;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

public class Monitor {
	
	private int[][] screen;
	private int screen_height;
	private int screen_width;
	WebSocketSession session;
	
	public Monitor(int screenHeight, int screenWidth, WebSocketSession session) {
		this.screen = new int[screenHeight][screenWidth];
		this.session = session;
		this.screen_height = screenHeight;
		this.screen_width = screenWidth;
	}
	
	public int getScreenHeight() {
		return screen_height;
	}
	
	public int getScreenWidth() {
		return screen_width;
	}
	
	public boolean setPixel(int screenY, int screenX, int bit, int oldPixel) {
		screen[screenY][screenX] = screen[screenY][screenX] ^ Character.getNumericValue(bit);
		return (screen[screenY][screenX] == 0) && (oldPixel == 1);
	}
	
	public void draw() {
		String jsonScreen = new Gson().toJson(new Frame(screen));
		try {
			session.sendMessage(new TextMessage(jsonScreen));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Frame {
		@SuppressWarnings("unused")
		int [][] screen;
		@SuppressWarnings("unused")
		String type = "frame";
		
		Frame(int[][] screen) {
			this.screen = screen;
		}
	}

}
