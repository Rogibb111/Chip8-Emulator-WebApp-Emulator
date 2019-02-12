package computer;

import java.util.Arrays;

public class Memory {

	private byte memoryArray[];
	static byte[] hexChar = {
	    (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
	    0x20, 0x60, 0x20, 0x20, 0x70, // 1
	    (byte) 0xF0, 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
	    (byte) 0xF0, 0x10, (byte) 0xF0, 0x10, (byte) 0xF0, // 3
	    (byte) 0x90, (byte) 0x90, (byte) 0xF0, 0x10, 0x10, // 4
	    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, 0x10, (byte) 0xF0, // 5
	    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
	    (byte) 0xF0, 0x10, 0x20, 0x40, 0x40, // 7
	    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
	    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, 0x10, (byte) 0xF0, // 9
	    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
	    (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
	    (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
	    (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
	    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
		(byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80 // F
	};
	
	Memory(byte[] gameBinary) {
		memoryArray = Arrays.copyOf(hexChar, 4096);
		System.arraycopy(gameBinary, 0, memoryArray, 0x200, gameBinary.length);
	}
	
	int readByte (int address) {
		return memoryArray[address] & 0xFF;
	}
	
	int[] readChunk (int address, int size) {
		byte[] chunk = Arrays.copyOfRange(this.memoryArray, address, address + size);
		int[] intChunk = new int[chunk.length];
		
		for(int i = 0; i < chunk.length; i++) {
			intChunk[i] = chunk[i] & 0xFF;
		}
		return intChunk;
	}
	
	void writeByte (int address, int byteToWrite) {
		memoryArray[address] = (byte)byteToWrite;
	}
	
	void writeChunk(int address, int[] byteArrayToWrite) {
		for (int i = 0; i < byteArrayToWrite.length; i++) {
			memoryArray[address + i] = (byte)byteArrayToWrite[i];
		}
	}
}
