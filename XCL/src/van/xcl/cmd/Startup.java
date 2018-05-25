package van.xcl.cmd;

import java.util.Map;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Startup implements Command {

	@Override
	public String name() {
		return "startup";
	}

	@Override
	public String description() {
		return "startup";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("command");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String command = args.get("command").toString();
		if ("*".equals(command)) {
			String startup = context.getStartup();
			console.output(CommonUtils.resolveJSONString(startup));
		} else {
			context.setStartup(command);
		}
		return new XCLVar(command);
	}

}
