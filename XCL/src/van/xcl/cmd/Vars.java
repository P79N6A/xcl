package van.xcl.cmd;

import java.util.Map;
import java.util.Map.Entry;

import van.xcl.util.json.JsonArray;
import van.xcl.util.json.JsonObject;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Vars implements Command {

	@Override
	public String name() {
		return "vars";
	}
	
	@Override
	public String description() {
		return "show all variables";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}
	
	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		StringBuilder sb = new StringBuilder("\n\n");
		for (Entry<String, Object> entry : context.getDataMap().entrySet()) {
			sb.append("\t");
			sb.append(entry.getKey());
			sb.append("\t=\t");
			Object obj = entry.getValue();
			if (obj instanceof JsonObject) {
				sb.append("[JSONObject]");
			} else if (obj instanceof JsonArray) {
				sb.append("[JSONArray]");
			} else {
				 sb.append(String.valueOf(obj));
			}
			sb.append("\n");
		}
		console.output(sb.toString());
		return new XCLVar();
	}
	
}
