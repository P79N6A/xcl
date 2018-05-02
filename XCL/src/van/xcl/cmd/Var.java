package van.xcl.cmd;

import java.util.Map;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Var implements Command {
	
	@Override
	public String name() {
		return "var";
	}
	
	@Override
	public String description() {
		return "variable";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("var", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!value.isString()) {
					throw new ParameterException("Syntax error: variable name must be a string");
				}
				if (value.toString().length() > 50) {
					throw new ParameterException("Syntax error: variable name is too long: " + value.toString().length());
				}
				if (context.containsVar(value.toString())) {
					throw new ParameterException("Syntax error: duplicate variable: " + value.toString());
				}
				if (context.containsCraft(value.toString())) {
					throw new ParameterException("Syntax error: duplicate script: " + value.toString());
				}
				if (context.getHandler().isCommand(value.toString())) {
					throw new ParameterException("Syntax error: \"" + value.toString() + "\" cannot be used as a variable name");
				}
			}
		}).setAutoResolve(false);
		parameters.add("value");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String key = args.get("var").toString();
		String value = args.get("value").toString();
		Object object = CommonUtils.parseJsonText(value);
		XCLVar var = new XCLVar(object);
		if (var.isNull()) {
			console.error(key + " is null");
		} else if (var.isString() || var.isBoolean() || var.isNumeric()) {
			context.setVar(key, var.toString());
			console.prompt(key + "=" + var.toString());
		} else if (var.isJsonObject()) {
			context.setVar(key, var.getJsonObject());
			console.prompt(key + "=JSONObject");
		} else if (var.isJsonArray()) {
			context.setVar(key, var.getJsonArray());
			console.prompt(key + "=JSONArray");
		}
		return new XCLVar(key); // key
	}

}
