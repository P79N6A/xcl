package van.xcl.cmd;

import java.util.Map;

import van.xcl.util.json.JsonArray;
import van.xcl.util.json.JsonObject;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.XCLConstants;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Echo implements Command {
	
	@Override
	public String name() {
		return XCLConstants.COMMAND_ECHO;
	}
	
	@Override
	public String description() {
		return "echo object";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("object_name");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("object_name").toString();
		if (context.containsCraft(input)) {
			String content = context.getCraft(input);
			console.input(content);
			console.prompt("[craft]");
			return new XCLVar(content);
		} else {
			XCLVar var = new XCLVar(input);
			if (var.isString() || var.isBoolean() || var.isNumeric()) {
				console.input(CommonUtils.resolveJSONString(var.toString()));
			} else if (var.isJsonObject()) {
				JsonObject obj = var.getJsonObject();
				console.input("\n" + CommonUtils.resolveJSONString(JsonObject.toJSONString(obj, true)));
			} else if (var.isJsonArray()) {
				JsonArray obj = var.getJsonArray();
				if (obj.size() > 0) {
					Object element = obj.get(0);
					if (element instanceof String) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0 ; i < obj.size() ; i++) {
							sb.append("\n" + (String) obj.get(i));
						}
						console.input(sb.toString());
					} else {
						console.input("\n" + CommonUtils.resolveJSONString(JsonObject.toJSONString(obj, true)));
					}
				}
			}
			console.prompt("[" + var.getType().name() + "]");
			return var;
		}
	}

}
