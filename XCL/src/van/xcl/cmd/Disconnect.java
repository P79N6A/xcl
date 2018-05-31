package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Disconnect implements Command {

	@Override
	public String name() {
		return XCLConstants.DISCONNECT_COMMAND;
	}
	
	@Override
	public String description() {
		return XCLConstants.DISCONNECT_COMMAND;
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		console.disconnect();
		return new XCLVar();
	}

}
