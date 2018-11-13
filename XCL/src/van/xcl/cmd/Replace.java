package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Replace implements Command {

	@Override
	public String name() {
		return "replace";
	}
	
	@Override
	public String description() {
		return "replace string";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("str");
		parameters.add("oldStr");
		parameters.add("newStr");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String str = args.get("str").toString();
		String oldStr = args.get("oldStr").toString();
		String newStr = args.get("newStr").toString();
		str = str.replace(oldStr, newStr);
		return new XCLVar(str);
	}

}
