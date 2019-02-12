package chip8;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import static computer.Computer.*;
import utils.GameMessage;
import utils.StartMessage;

@CrossOrigin
@RestController
public class RestAPIController {
	
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public StartMessage startGame(@RequestBody Map<String, String> body) {
		String fileName = body.get("filename");
		String frequency = body.get("frequency");
		String id = createInstance(fileName, Integer.parseInt(frequency));

		return new StartMessage(id);
	}
	
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public void stopGame(@RequestBody Map<String, String> body) throws IOException {
		String id = body.get("id");
		removeInstance(id);
	}
	
	@RequestMapping("/games")
	public GameMessage[] games() {
		List<GameMessage> results = new ArrayList<GameMessage>();
		File[] files = new File("src/main/resources/games").listFiles();
		
		for(File file : files) {
			if(file.isFile()) {
				String name = file.getName();
				results.add(new GameMessage(name.replace(".chip8", "").replace(".ch8", ""), name));
			}
		}
		
		return results.toArray(new GameMessage[results.size()]);
	}
}
