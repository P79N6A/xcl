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

public class Crafts implements Command {

	@Override
	public String name() {
		return "crafts";
	}
	
	@Override
	public String description() {
		return "show all crafts";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}
	
	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		StringBuilder sb = new StringBuilder("\n\n");
		List<String> crafts = new ArrayList<String>();
		for (Entry<String, String> entry : context.getCrafts().entrySet()) {
			crafts.add(entry.getKey());
		}
		Collections.sort(crafts);
		for (String craft : crafts) {
			sb.append("\t" + craft + "\n");
		}
		console.input(sb.toString());
		return new XCLVar();
	}
	
}
