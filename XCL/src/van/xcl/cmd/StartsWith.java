package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class StartsWith implements Command {

	@Override
	public String name() {
		return "startswith";
	}

	@Override
	public String description() {
		return "starts with";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("text");
		parameters.add("prefix").setAutoResolve(false);;
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String text = args.get("text").toString();
		String prefix = args.get("prefix").toString();
		return new XCLVar(text.startsWith(prefix));
	}

}
