package van.xcl.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Start implements Command {
	
	@Override
	public String name() {
		return "start";
	}

	@Override
	public String description() {
		return "cmd /c start";
	}
	
	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("cmd");
		return parameters;
	}
	
	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("cmd").toString();
		try {
			String cmdInput = input;
			File file = new File(context.getPath(), input);
			if (file.exists()) {
				cmdInput = file.getAbsolutePath();
			}
			String cmd = "cmd /c start \"\" \"" + cmdInput + "\"";
			console.prompt(cmd);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			console.error("IOException: " + e.getMessage());
		}
		return new XCLVar(input);
	}

}
