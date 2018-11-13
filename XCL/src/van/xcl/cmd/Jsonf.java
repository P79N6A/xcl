package van.xcl.cmd;
import java.util.Map;

import van.xcl.util.json.Json;
import van.xcl.util.json.JsonObject;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Jsonf implements Command {
	
	@Override
	public String name() {
		return "jsonf";
	}
	
	@Override
	public String description() {
		return "json format";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("json_text");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("json_text").toString();
		Json json = (Json) JsonObject.parse(input);
		return new XCLVar(json);
	}

}
