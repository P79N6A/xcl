package van.xcl;

import java.util.Map;

import van.xcl.XCLCmdParser.XCLNode;

public interface Command {
	public String name();
	public String description();
	public Parameters parameters();
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException;
}