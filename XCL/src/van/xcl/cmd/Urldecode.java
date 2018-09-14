package van.xcl.cmd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Urldecode implements Command {

	@Override
	public String name() {
		return "urldecode";
	}

	@Override
	public String description() {
		return "URL decode";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("string");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String string = args.get("string").toString();
		try {
			string = URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new CommandException(e.getLocalizedMessage());
		}
		return new XCLVar(string);
	}

}
