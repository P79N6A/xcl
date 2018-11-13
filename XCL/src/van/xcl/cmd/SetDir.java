package van.xcl.cmd;

import java.io.File;
import java.util.Map;

import van.xcl.Command;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class SetDir implements Command {

	@Override
	public String name() {
		return "setdir";
	}
	
	@Override
	public String description() {
		return "set working dir";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("dir", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				File file = new File(value.toString());
				if (!file.exists() || !file.isDirectory()) {
					throw new ParameterException("Directory not found: " + file.getAbsolutePath());
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		File dir = new File(args.get("dir").toString(true));
		String path = dir.getAbsolutePath();
		context.setPath(path);
		console.output("Current Working Directory: \"" + path + "\"");
		return new XCLVar(path);
	}
}
