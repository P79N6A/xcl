package van.xcl;

import java.util.Map;

public interface Command {
	public String name();
	public String description();
	public Parameters parameters();
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException;
}