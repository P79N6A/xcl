package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Disconnect implements Command {

	@Override
	public String name() {
		return "disconnect";
	}
	
	@Override
	public String description() {
		return "disconnect";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		console.disconnect();
		return new XCLVar();
	}

}
