package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class IfVar implements Command {

	@Override
	public String name() {
		return "ifvar";
	}
	
	@Override
	public String description() {
		return "if var is defined or not";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("var").setAutoResolve(false);;
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String varName = args.get("var").toString();
		return new XCLVar(context.containsVar(varName));
	}

}
