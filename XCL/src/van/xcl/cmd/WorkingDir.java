package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class WorkingDir implements Command {

	@Override
	public String name() {
		return "workdir";
	}
	
	@Override
	public String description() {
		return "working dir";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String path = context.getPath();
		console.output(path);
		return new XCLVar(path);
	}
}
