package computer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import devices.Keyboard;
import devices.Monitor;
import devices.Soundcard;

public class Computer {
	
	private int screen_height = 32;
	private int screen_width = 64;
	private int frequency;
	private Timer timer = new Timer();
	
	private Memory memory;
	
	private CPU cpu;
	
	public Keyboard keyboard;
		
	private Monitor monitor;
	
	private Soundcard soundcard;
	
	private WebSocketSession session;
	
	private static Map<UUID, Computer> instances = new HashMap<>();
	
	private Computer(String filename, int frequency) {
		try {
			this.frequency = (int) (((float) 1/frequency)*1000);
			File gameBinary = new File("src/main/resources/games/".concat(filename));
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(gameBinary)));
			byte[] binaryArray = new byte[(int) gameBinary.length()];
			
			in.read(binaryArray);
			in.close();
			
			memory = new Memory(binaryArray);
			keyboard = new Keyboard();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(WebSocketSession session) {
		this.session = session;
		monitor = new Monitor(screen_height, screen_width, session);
		soundcard = new Soundcard(session);
		cpu = new CPU(keyboard, monitor, soundcard, memory);
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				cpu.execute();
			}
		};
		
		timer.schedule(task, 0, frequency);
	}
	
	public void destroy() throws IOException {
		timer.cancel();
		session.close();
	}
	
	public static String createInstance(String game, int frequency) {
		UUID id = UUID.randomUUID();
		
		instances.put(id, new Computer(game, frequency));
		
		return id.toString();
	}
	
	public static Computer getInstance(String id) {
		return instances.get(UUID.fromString(id));
	}
	
	public static void removeInstance(String id) throws IOException {
		UUID uuid = UUID.fromString(id);
		if(instances.containsKey(uuid)) {
			Computer instance = instances.remove(uuid);
			instance.destroy();
		}
	}
}
