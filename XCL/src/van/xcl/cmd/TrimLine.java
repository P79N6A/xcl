package van.xcl.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class TrimLine implements Command {

	@Override
	public String name() {
		return "trimline";
	}

	@Override
	public String description() {
		return "trim line";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("text");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String text = args.get("text").toString();
		try {
			BufferedReader br = new BufferedReader(new StringReader(text));
			StringBuilder s = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				s.append(line);
			}
			br.close();
			return new XCLVar(s.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new XCLVar(text);
	}

}
