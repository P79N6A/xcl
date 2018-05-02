package van.xcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XCLCmdHolder {

	private Map<String, Command> commands = new HashMap<String, Command>();
	private List<Resolver> resolvers = new ArrayList<Resolver>();
	
	public void addCommand(String name, Command command) {
		commands.put(name, command);
		if (command instanceof Resolver) {
			resolvers.add((Resolver) command);
		}
	}
	
	public Command getCommand(String name) {
		return commands.get(name);
	}
	
	public boolean isCommand(String name) {
		return commands.containsKey(name);
	}
	
	public List<Resolver> allResolvers() {
		return new ArrayList<Resolver>(resolvers);
	}
	
	public Map<String, Command> allCommands() {
		Map<String, Command> map = new HashMap<String, Command>();
		for (Entry<String, Command> c : commands.entrySet()) {
			map.put(c.getKey(), c.getValue());
		}
		return map;
	}
}
