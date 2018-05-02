package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class IndexOf implements Command {

	@Override
	public String name() {
		return "indexof";
	}

	@Override
	public String description() {
		return "index of";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("string");
		parameters.add("substr");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String string = args.get("string").toString();
		String substr = args.get("substr").toString();
		return new XCLVar(string.indexOf(substr));
	}

}
