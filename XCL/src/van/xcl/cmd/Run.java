package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Run implements Command {
	
	@Override
	public String name() {
		return "run";
	}
	
	@Override
	public String description() {
		return "run command script";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("script");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("script").toString();
		console.output("run:\n" + input);
		console.run(input);
		return new XCLVar();
	}
}
