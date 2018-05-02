package van.xcl.cmd;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.Constants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Edit implements Command {
	
	@Override
	public String name() {
		return Constants.EDIT_COMMAND;
	}
	
	@Override
	public String description() {
		return "edit object";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("object_name", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String key = value.toString();
				if (!context.containsVar(key) && !context.containsCraft(key)) {
					throw new ParameterException("\"" + value.toString() + "\" is undefined");
				}
			}
		}).setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String key = args.get("object_name").toString();
		if (context.containsVar(key)) {
			XCLVar var = context.getXCLVar(key);
			if (var != null) {
				String newValue = null;
				console.prompt("Edit [" + key + "]");
				if (var.isString() || var.isBoolean() || var.isNumeric()) {
					newValue = console.getTextInput(var.toString(), key);
				} else if (var.isJsonObject()) {
					JSONObject obj = var.getJsonObject();
					newValue = console.getTextInput(JSONObject.toJSONString(obj, true), key);
				} else if (var.isJsonArray()) {
					JSONArray obj = var.getJsonArray();
					newValue = console.getTextInput(JSONObject.toJSONString(obj, true), key);
				}
				if (newValue != null) {
					Object object = CommonUtils.parseJsonText(newValue);
					XCLVar newVar = new XCLVar(object);
					if (newVar.isString()) {
						context.setVar(key, newVar.getString());
						console.prompt(key + "=" + newVar.getString());
					} else if (newVar.isJsonObject()) {
						context.setVar(key, newVar.getJsonObject());
						console.prompt(key + "=JSONObject");
					} else if (newVar.isJsonArray()) {
						context.setVar(key, newVar.getJsonArray());
						console.prompt(key + "=JSONArray");
					}
					console.output("[" + key + "] is updated.");
					return newVar;
				} else {
					return var;
				}
			}
		} else if (context.containsCraft(key)) {
			String content = context.getCraft(key);
			String newValue = console.getTextInput(content, key);
			if (newValue != null) {
				context.setCraft(key, newValue);
				return new XCLVar(newValue);
			}
		}
		console.output("\"" + key + "\" is undefined");
		return new XCLVar(key);
	}
}
