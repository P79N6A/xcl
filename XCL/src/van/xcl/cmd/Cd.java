package van.xcl.cmd;

import java.io.File;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Cd implements Command {


	@Override
	public String name() {
		return "cd";
	}
	
	@Override
	public String description() {
		return "change directory";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("path", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String path = value.toString();
				if (!"..".equals(path)) {
					File relFile = new File(path);
					File absFile = new File(context.getPath(), path);
					if (!relFile.exists() && !absFile.exists()) {
						new ParameterException("The specified path cannot be found: " + path);
					}
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("path").toString(true);
		String path = context.getPath();
		if ("..".equals(input) && new File(path).getParent() != null) {
			File absPath = new File(path).getParentFile();
			context.setPath(absPath.getAbsolutePath());
			console.output(absPath.getAbsolutePath());
		} else {
			File absPath = new File(input);
			File relPath = new File(path, input);
			if (absPath.exists() && absPath.isDirectory()) {
				context.setPath(absPath.getAbsolutePath());
				console.output(absPath.getAbsolutePath());
			} else if (relPath.exists() && relPath.isDirectory()) {
				context.setPath(relPath.getAbsolutePath());
				console.output(relPath.getAbsolutePath());
			} else {
				console.error("The specified path cannot be found");
			}
		}
		console.title(context.getPath());
		return new XCLVar(context.getPath());
	}

}
