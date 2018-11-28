package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class ClearVars implements Command {

	@Override
	public String name() {
		return "clearvars";
	}
	
	@Override
	public String description() {
		return "clear all variables";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}
	
	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		context.clearVars();
		return new XCLVar();
	}
	
}
