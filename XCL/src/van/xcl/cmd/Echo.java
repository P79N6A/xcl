package van.xcl.cmd;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.Constants;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Echo implements Command {
	
	@Override
	public String name() {
		return Constants.ECHO_COMMAND;
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
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("object_name").toString();
		if (context.containsCraft(input)) {
			String content = context.getCraft(input);
			console.output(content);
			console.prompt("[craft]");
			return new XCLVar(content);
		} else {
			XCLVar var = new XCLVar(input);
			if (var.isString() || var.isBoolean() || var.isNumeric()) {
				console.output(CommonUtils.resolveJSONString(var.toString()));
			} else if (var.isJsonObject()) {
				JSONObject obj = var.getJsonObject();
				console.output("\n" + CommonUtils.resolveJSONString(JSONObject.toJSONString(obj, true)));
			} else if (var.isJsonArray()) {
				JSONArray obj = var.getJsonArray();
				if (obj.size() > 0) {
					Object element = obj.get(0);
					if (element instanceof String) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0 ; i < obj.size() ; i++) {
							sb.append("\n" + (String) obj.get(i));
						}
						console.output(sb.toString());
					} else {
						console.output("\n" + CommonUtils.resolveJSONString(JSONObject.toJSONString(obj, true)));
					}
				}
			}
			console.prompt("[" + var.getType().name() + "]");
			return var;
		}
	}

}
