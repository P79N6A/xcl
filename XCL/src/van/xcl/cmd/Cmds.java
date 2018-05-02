package van.xcl.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Cmds implements Command {

	@Override
	public String name() {
		return "cmds";
	}
	
	@Override
	public String description() {
		return "show all commands";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		List<String> cmdList = new ArrayList<String>();
		for (Entry<String, Command> entry : console.commands().entrySet()) {
			cmdList.add("\n    " + entry.getKey() + " : \t" + entry.getValue().description());
		}
		Collections.sort(cmdList);
		StringBuilder cmds = new StringBuilder("commands:");
		for (String cmd : cmdList) {
			cmds.append(cmd);
		}
		console.output(cmds.toString());
		return new XCLVar();
	}

}
