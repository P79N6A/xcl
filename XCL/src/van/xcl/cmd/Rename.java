package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Rename implements Command {
	
	@Override
	public String name() {
		return "rename";
	}
	
	@Override
	public String description() {
		return "rename object";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("<object_name>-><new_object_name>", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String str = value.toString();
				if (!str.contains("->") || str.length() < 4) {
					throw new ParameterException("Please enter new and old names and separate them with a \"->\"");
				}
			}
		}).setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String name = args.get("<object_name>-><new_object_name>").toString();
		String objectName = name.split("->")[0];
		String objectNewName = name.split("->")[1];
		if (context.containsName(objectName)) {
			if (!context.containsName(objectNewName)) {
				if (context.containsVar(objectName)) {
					context.renameVar(objectName, objectNewName);
					console.output("[" + objectName + "] --> [" + objectNewName + "] variable has been renamed");
				} else if (context.containsCraft(objectName)) {
					context.renameCraft(objectName, objectNewName);
					console.removeDynamicKey(objectName);
					console.addDynamicKey(objectNewName);
					console.output("[" + objectName + "] --> [" + objectNewName + "] craft has been renamed");
				}
			} else {
				console.error("[" + objectName + "] object already exists");
			}
		} else {
			console.error("[" + objectName + "] object does not exist");
		}
		return new XCLVar(objectName);
	}

}
