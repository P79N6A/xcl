package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Input implements Command {

	@Override
	public String name() {
		return "/";
	}
	
	@Override
	public String description() {
		return "multi-line input";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String argName = node.getParaName();
		XCLCommandNode par = node;
		while (par.getParent() != null) {
			par = par.getParent();
		}
		console.prompt(par.toString() + " --> [" + argName + "]");
		String value = console.getTextInput(null, argName).trim();
		if (value != null) {
			console.prompt(value);
			XCLVar var = new XCLVar(value, true);
			return var;
		}
		return new XCLVar();
	}
	
}
