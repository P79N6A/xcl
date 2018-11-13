package van.xcl.cmd;

import java.io.File;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Ls implements Command {
	
	@Override
	public String name() {
		return "ls";
	}
	
	@Override
	public String description() {
		return "list current directory";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String path = context.getPath();
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				StringBuilder sb = new StringBuilder(path);
				for (File f : file.listFiles()) {
					String filename = f.getName();
					String fileattr = f.isDirectory() ? "+" : "-";
					sb.append("\n    " + fileattr + " " + filename);
				}
				console.output(sb.toString());
			}
		}
		return new XCLVar(path);
	}
	
}
