package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Switch implements Command {
	@Override
	public String name() {
		return "switch";
	}
	
	@Override
	public String description() {
		return "switch context";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("context_name");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String contextName = args.get("context_name").toString();
		String orginal = context.getHandler().currentContext();
		if (context.getHandler().switchContext(contextName)) {
			String current = context.getHandler().currentContext();
			console.output("Context is switched: " + orginal + "-->" + current);
		} else {
			console.output("Context switching failed.");
		}
		return new XCLVar();
	}

}
