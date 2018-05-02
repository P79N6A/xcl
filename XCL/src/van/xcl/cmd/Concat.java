package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Concat implements Command {

	@Override
	public String name() {
		return "concat";
	}
	
	@Override
	public String description() {
		return "concat two strings";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("str1");
		parameters.add("str2");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String str1 = args.get("str1").toString();
		String str2 = args.get("str2").toString();
		String str = str1 + str2;
		return new XCLVar(str);
	}

}
