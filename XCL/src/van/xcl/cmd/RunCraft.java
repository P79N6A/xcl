package van.xcl.cmd;

import java.util.List;
import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.Resolver;
import van.xcl.XCLContext;
import van.xcl.XCLDynamicParameter;
import van.xcl.XCLCommandNode;
import van.xcl.XCLResult;
import van.xcl.XCLVar;

public class RunCraft implements Command, Resolver {

	@Override
	public String name() {
		return XCLConstants.COMMAND_RUN_CRAFT;
	}

	@Override
	public String description() {
		return "run craft";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("craft_name", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!context.containsCraft(value.toString())) {
					throw new ParameterException("\"" + value.toString() + "\" script not found");
				}
			}
		}).setAutoResolve(false);
		parameters.add("paras", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String strval = value.toString();
				if (!"".equals(strval)) {
					XCLDynamicParameter.validate(value);
				}
			}
		}).setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String name = args.get("craft_name").toString();
		String craft = context.getCraft(name);
		XCLDynamicParameter map = XCLDynamicParameter.resolveDynamicParameter(args.get("paras").toString());
		XCLContext current = context.clone();
		if (map != null) {
			for (String k : map.keySet()) {
				current.setVar(k, map.getValue(k));
			}
		}
		long startInMillis = System.currentTimeMillis();
		console.info("[" + name + "] craft is started");
		XCLResult result = console.execute(craft, current);
		if (result.isSuccess()) {
			console.info("[" + name + "] craft is done [Time Used: " + (System.currentTimeMillis() - startInMillis) + "ms]");
		}
		if (result.isSuccess()) {
			List<XCLVar> list = result.getResults();
			return list.get(list.size() - 1);
		}
		return new XCLVar();
	}
	
	@Override
	public void resolve(List<String> commands, XCLContext context) {
		for (int i = 0 ; i < commands.size(); i++) {
			String command = commands.get(i);
			if (context.containsCraft(command)) {
				String previousCommand =  i > 0 ? commands.get(i - 1) : null;
				if (!XCLConstants.COMMAND_RUN_CRAFT.equals(previousCommand)
						&& !XCLConstants.COMMAND_CRAFT.equals(previousCommand)
						&& !XCLConstants.COMMAND_REMOVE.equals(previousCommand)
						&& !XCLConstants.COMMAND_EDIT.equals(previousCommand)
						&& !XCLConstants.COMMAND_ECHO.equals(previousCommand)
						) {
					commands.add(i, XCLConstants.COMMAND_RUN_CRAFT);
				}
			}
		}
	}

}
