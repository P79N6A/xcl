package van.xcl.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import van.xcl.Command;
import van.xcl.Parameters;
import van.xcl.XCLStreamPrinter;
import van.xcl.XCLStreamPrinter.Type;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Exec implements Command {
	
	@Override
	public String name() {
		return "exec";
	}
	
	@Override
	public String description() {
		return "execute commands";
	}
	
	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("script");
		return parameters;
	}

	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("script").toString(true);
		try {
			console.prompt(input);
			Process proc = Runtime.getRuntime().exec(input, null, new File(context.getPath()));
			XCLStreamPrinter outHandler = new XCLStreamPrinter(proc.getInputStream(), "exec-out");
			XCLStreamPrinter errHandler = new XCLStreamPrinter(proc.getErrorStream(), "exec-err");
			outHandler.print(context, console, Type.INF);
			errHandler.print(context, console, Type.ERR);
			int exitVal = proc.waitFor();
			console.info("[exec-out] exit value: " + exitVal);
		} catch (IOException e) {
			console.output("IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			console.output("InterruptedException: " + e.getMessage());
		}
		return new XCLVar();
	}
}
