package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Remove implements Command {
	
	@Override
	public String name() {
		return "remove";
	}
	
	@Override
	public String description() {
		return "remove object";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("object_name").setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String objectName = args.get("object_name").toString();
		if (objectName != null) {
			if (context.containsVar(objectName)) {
				context.removeVar(objectName);
				console.output("[" + objectName + "] variable has been removed");
			} else if (context.containsCraft(objectName)) {
				context.removeCraft(objectName);
				console.removeDynamicKey(objectName);
				console.output("[" + objectName + "] craft has been removed");
			} else {
				console.error("[" + objectName + "] object does not exist");
			}
		}
		return new XCLVar(objectName);
	}

}
