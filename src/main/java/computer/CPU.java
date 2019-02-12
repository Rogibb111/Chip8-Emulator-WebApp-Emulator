package computer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import devices.*;

public class CPU {
	private int[] v = new int[16];
	
	private int[] stack = new int[16];
	
	private int pc = 0x200;
	
	private int sp = 0;
	
	private int i = 0;
	
	private int delayTimer = 0;
	
	private int soundTimer = 0;
	
	private Keyboard keyboard;
	
	private GPU gpu;
	
	private Soundcard soundcard;
	
	private Memory memory;
	
	private Random rand = new Random();
	
	private boolean halted = false;
	
	private int keyToPress = 0;
	
	private static Map<Integer, Integer> hexDisplayMap;
	
	
	static {
		hexDisplayMap = new HashMap<Integer,Integer>();
		hexDisplayMap.put(0x0, 0x00);
		hexDisplayMap.put(0x1, 0x05);
		hexDisplayMap.put(0x2, 0x0A);
		hexDisplayMap.put(0x3, 0x0F);
		hexDisplayMap.put(0x4, 0x14);
		hexDisplayMap.put(0x5, 0x19);
		hexDisplayMap.put(0x6, 0x1E);
		hexDisplayMap.put(0x7, 0x23);
		hexDisplayMap.put(0x8, 0x28);
		hexDisplayMap.put(0x9, 0x2D);
		hexDisplayMap.put(0xA, 0x32);
		hexDisplayMap.put(0xB, 0x37);
		hexDisplayMap.put(0xC, 0x3C);
		hexDisplayMap.put(0xD, 0x41);
		hexDisplayMap.put(0xE, 0x46);
		hexDisplayMap.put(0xF, 0x4B);
	}
	
	CPU(Keyboard keyboard, Monitor monitor, Soundcard soundcard, Memory memory) {
		this.keyboard = keyboard;
		this.gpu = new GPU(monitor);
		this.soundcard = soundcard;
		this.memory = memory;
	}
	
	public void execute() {
		if(halted && keyboard.isKeyPressed(keyToPress)) {
			halted = false;
		} else if (halted) {
			return;
		}
		
		int opcode = memory.readByte(pc) << 8 | memory.readByte(pc + 1);
		int x = (opcode & 0x0F00) >> 8;
		int y = (opcode & 0x00F0) >> 4;
		
		switch(opcode & 0xF000) {
			case 0x0000:
				switch(opcode & 0x00FF) {
					case 0xE0:
						gpu.clearScreen();
						break;
					case 0xEE:
						pc = stack[sp];
						sp = sp - 1;
						break;
					default:
						pc = 0x0FFF & opcode;
				}
				break;
			case 0x1000:
				pc = 0x0FFF & opcode;
				break;
			case 0x2000:
				sp = sp + 1;
				stack[sp] = pc;
				pc = opcode & 0x0FFF;
				break;
			case 0x3000:
				if(v[x] == (opcode & 0x00FF)) {
					pc = pc + 2;
				}
				break;
			case 0x4000:
				if(v[x] != (opcode & 0x00FF)) {
					pc = pc + 2;
				}
				break;
			case 0x5000:
				if(v[x] == v[(opcode & 0x00F0) >> 4]) {
					pc = pc + 2;
				}
				break;
			case 0x6000:
				v[x] = (opcode & 0x00FF); 
				break;
			case 0x7000:
				int register = (opcode & 0x0F00) >> 8;
				v[register] = v[register] + (opcode & 0x00FF);
				v[register] = v[register] & 0xFF;
				break;
			case 0x8000:
				switch(opcode & 0x000F) {
					case 0x0:
						v[x] = v[(opcode & 0x00F0) >> 4];
						break;
					case 0x1:
						v[x] = v[x] | v[y];
						break;
					case 0x2:
						v[x] = v[x] & v[y];
						break;
					case 0x3:
						v[x] = v[x] ^ v[y];
						break;
					case 0x4:
						v[x] = v[x] + v[y];
						v[0xF] = 0;
						
						if (v[x] > 0xFF) {
							v[0xF] = 1;
							v[x] = v[x] & 0xFF;
						}
						break;
					case 0x5:
						v[0xF] = 0;
						
						if (v[x] > v[y]) {
							v[0xF] = 1;
						}
						
						v[x] = v[x] - v[y];  
						break;
					case 0x6:
						v[0xF] = 0x01 & v[x];
						v[x] = v[x] >> 1;
						break;
					case 0x7:
						v[0xF] = 0;
						
						if(v[y] > v[x]) {
							v[0xF] = 1;
						}
						
						v[x] = v[y] - v[x];
						break;
					case 0xE:
						v[0xF] = v[x] >> 7;
						v[x] = v[x] * 2;
						break;
				}
				break;
			case 0x9000:
				if (v[x] != v[y]) {
					pc = pc + 2;
				}
				break;
			case 0xA000:
					i = opcode & 0x0FFF;
				break;
			case 0xB000:
				pc = (0x0FFF & opcode) + v[0];
				break;
			case 0xC000:
				int randNum = rand.nextInt(0xFF);
				
				v[x] = randNum & (0xFF & opcode); 
				break;
			case 0xD000:
				int[] sprite = memory.readChunk(i, opcode & 0x000F);
				
				v[0xF] = gpu.writeSprite(v[x], v[y], sprite);
				break;
			case 0xE000:
				switch(opcode & 0x000F) {
					case 0xE:
						if (keyboard.isKeyPressed(v[x])) {
							pc = pc + 2;
						}
						break;
					case 0x1:
						if (!keyboard.isKeyPressed(v[x])) {
							pc = pc + 2;
						}
						break;
				}
				break;
			case 0xF000:
				int lastDigit = opcode & 0x000F; 
				if(lastDigit == 0x5) {
					switch(opcode & 0x00FF) {
						case 0x15:
							delayTimer = v[x];
							break;
						case 0x55:
							for (int k = 0; k <= x; k++) {
								memory.writeByte(i+k, v[k]);
							}
							break;
						case 0x65:
							for (int j = 0; j <= x; j++) {
								v[j] = memory.readByte(i+j);
							}
							break;
					}
				} else {
					switch(lastDigit) {
					case 0x7:
						v[x] = delayTimer;
						break;
					case 0xA:
						halted = true;
						keyToPress = x;
						break;
					case 0x8:
						soundTimer = v[x];
						break;
					case 0xE:
						i = i + v[x];
						break;
					case 0x9:
						i = hexDisplayMap.get(v[x]);
						break;
					case 0x3:
						String strVal = Integer.toString(v[x]);
						String fullVal = "000".substring(strVal.length()).concat(strVal);
						int [] numArr = Arrays.stream(fullVal.split("")).mapToInt(Integer::parseInt).toArray();
						
						memory.writeChunk(i,numArr);
						break;
					}
				}
				break;
		}
		
		if(delayTimer > 0) {
			delayTimer--;
		}
		
		if(soundTimer > 0) {
			soundTimer--;
			soundcard.playSound();
		} 
		
//		System.out.println("________________________________");
//		System.out.println("PC: "+pc);
//		System.out.println("Stack: "+Arrays.toString(stack));
//		System.out.println("Sp: "+sp);
//		System.out.println("V: "+Arrays.toString(v));
//		System.out.println("DelayTimer: "+ delayTimer);
//		System.out.println("SoundTimer: "+ soundTimer);
//		System.out.println("Opcode "+Integer.toHexString(opcode));
		
		switch (0xF000 & opcode) {
			case 0x1000: 
			case 0x2000:
			case 0xB000:
				break;
			default:
				pc = pc + 2;
		}
	}
	
}
