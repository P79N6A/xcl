package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Clear implements Command {

	@Override
	public String name() {
		return "clear";
	}
	
	@Override
	public String description() {
		return "clear";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		console.clear();
		return new XCLVar();
	}
}
