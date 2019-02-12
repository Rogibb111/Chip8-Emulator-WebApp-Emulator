package computer;

import devices.Monitor;

public class GPU {
	private int[][] screen;
	
	private int screen_height;
	
	private int screen_width;
	
	private Monitor monitor;
	
	GPU(Monitor monitor) {
		this.monitor = monitor;
		this.screen_height = monitor.getScreenHeight() ;
		this.screen_width = monitor.getScreenWidth();
		clearScreen();
	}
	
	int writeSprite(int startX, int startY, int[] sprite) {
		int collision = 0;
		
		for(int i = 0; i < sprite.length; i++) {
			String line = Integer.toString(sprite[i], 2);
			String fullByte = "00000000".substring(line.length()).concat(line);
			int screenY = (startY + i) % screen_height;
			
			for(int j = 0; j < 8; j++) {
				int screenX = (startX + j) % screen_width;
				int oldPixel = screen[screenY][screenX];
				char bit = fullByte.charAt(j);
				
				if(monitor.setPixel(screenY, screenX, bit, oldPixel)) {
					collision = 1;
				}
			}
		}
		monitor.draw();
		return collision;
	}
	
	int[][] getScreen() {
		return screen;
	}
	
	void clearScreen() {
		screen = new int[this.screen_height][this.screen_width];
	}
}
