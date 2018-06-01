package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLStartup;
import van.xcl.XCLStartupParas;
import van.xcl.XCLVar;

public class Startup implements Command {
	@Override
	public String name() {
		return "startup";
	}
	
	@Override
	public String description() {
		return "startup context";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("context_name");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String contextName = args.get("context_name").toString();
		XCLStartupParas paras = new XCLStartupParas();
		paras.setPara("context", contextName);
		XCLStartup.startup(paras);
		return new XCLVar(contextName + " is startup");
	}

}
