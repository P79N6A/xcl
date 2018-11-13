package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Version implements Command {
	
	@Override
	public String name() {
		return "ver";
	}
	
	@Override
	public String description() {
		return "version";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		console.output(XCLConstants.VERSION_PROMPT);
		return new XCLVar(XCLConstants.VERSION_PROMPT);
	}

}
