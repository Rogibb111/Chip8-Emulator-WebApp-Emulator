package utils;

public class GameMessage {
	public String name;
	public String fileName;
	
	public GameMessage(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFileName() {
		return this.fileName;
	}
}
