package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Help implements Command {


	@Override
	public String name() {
		return "help";
	}
	
	@Override
	public String description() {
		return "help";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		StringBuilder help = new StringBuilder("\n");
		help.append("\t1, Enter the \"cmds\" command to show all available commands.\n");
		help.append("\t2, Enter the \"crafts\" command to show all available crafts.\n");
		help.append("\t3, Enter the \"vars\" command to show all defined variables.\n");
		console.output(help.toString());
		return new XCLVar();
	}

}
