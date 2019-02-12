package devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {

	static Map<Double, Integer> keyMap;
	static {
		keyMap = new HashMap<>();
		keyMap.put(88.0, 0x0);
		keyMap.put(49.0, 0x1);
		keyMap.put(50.0, 0x2);
		keyMap.put(51.0, 0x3);
		keyMap.put(81.0, 0x4);
		keyMap.put(87.0, 0x5);
		keyMap.put(69.0, 0x6);
		keyMap.put(65.0, 0x7);
		keyMap.put(83.0, 0x8);
		keyMap.put(68.0, 0x9);
		keyMap.put(90.0, 0xA);
		keyMap.put(67.0, 0xB);
		keyMap.put(52.0, 0xC);
		keyMap.put(82.0, 0xD);
		keyMap.put(70.0, 0xE);
		keyMap.put(86.0, 0xF);
	}

	private boolean[] pressedKeys;

	public Keyboard() {
		this.pressedKeys = new boolean[16];
	}

	public synchronized void updateKeyboard(ArrayList<?> pressedKeys) {
		this.pressedKeys = new boolean[16];
		for (Object key : pressedKeys) {
			Integer keyValue = keyMap.get(key);
			if (keyValue != null) {
				this.pressedKeys[keyValue] = true;
			}

		}
	}

	public synchronized boolean[] getCurrentKeyboard() {
		return this.pressedKeys;
	}

	public synchronized boolean isKeyPressed(int key) {
		return pressedKeys[key];
	}
}
