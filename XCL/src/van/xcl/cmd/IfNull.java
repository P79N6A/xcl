package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class IfNull implements Command {

	@Override
	public String name() {
		return "ifnull";
	}
	
	@Override
	public String description() {
		return "if null";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("var");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		XCLVar var = args.get("var");
		if (var.isNull() || "".equals(var.toString()) || "null".equals(var.toString())) {
			return new XCLVar("true");
		}
		return new XCLVar("false");
	}

}
