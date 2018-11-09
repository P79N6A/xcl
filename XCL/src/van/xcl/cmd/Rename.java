package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Rename implements Command {
	
	@Override
	public String name() {
		return "rename";
	}
	
	@Override
	public String description() {
		return "rename object";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("&<source_name>", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String str = value.toString();
				if (!str.startsWith(XCLConstants.BUILTIN_VAL_PERFIX)) {
					throw new ParameterException("object name should be start with: " + XCLConstants.BUILTIN_VAL_PERFIX);
				}
			}
		}).setAutoResolve(false);
		parameters.add("&<target_name>", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String str = value.toString();
				if (!str.startsWith(XCLConstants.BUILTIN_VAL_PERFIX)) {
					throw new ParameterException("object name should be start with: " + XCLConstants.BUILTIN_VAL_PERFIX);
				}
			}
		}).setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String sourceName = args.get("&<source_name>").toString().replaceFirst(XCLConstants.BUILTIN_VAL_PERFIX, "");
		String targetName = args.get("&<target_name>").toString().replaceFirst(XCLConstants.BUILTIN_VAL_PERFIX, "");
		if (context.containsName(sourceName)) {
			if (!context.containsName(targetName)) {
				if (context.containsVar(sourceName)) {
					context.renameVar(sourceName, targetName);
					console.output("[" + sourceName + "] --> [" + targetName + "] variable has been renamed");
				} else if (context.containsCraft(sourceName)) {
					context.renameCraft(sourceName, targetName);
					console.removeDynamicKey(sourceName);
					console.addDynamicKey(targetName);
					console.output("[" + sourceName + "] --> [" + targetName + "] craft has been renamed");
				}
			} else {
				console.error("[" + sourceName + "] object already exists");
			}
		} else {
			console.error("[" + sourceName + "] object does not exist");
		}
		return new XCLVar(targetName);
	}

}
