package van.xcl.cmd;

import java.util.Map;

import van.util.sf.StringFilter;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Accept implements Command {

	@Override
	public String name() {
		return "accept";
	}

	@Override
	public String description() {
		return "accept";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("string");
		parameters.add("filter");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String string = args.get("string").toString();
		String filter = args.get("filter").toString();
		StringFilter sf = new StringFilter(filter);
		return new XCLVar(sf.accept(string));
	}

}
