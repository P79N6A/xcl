package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Craft implements Command {

	@Override
	public String name() {
		return XCLConstants.CRAFT_COMMAND;
	}

	@Override
	public String description() {
		return "craft";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("craft_name", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!context.isValidObjectName(value.toString())) {
					throw new ParameterException("Syntax error: invalid craft name: " + value.toString());
				}
				if (context.containsVar(value.toString())) {
					throw new ParameterException("Syntax error: duplicate variable: " + value.toString());
				}
				if (context.containsCraft(value.toString())) {
					throw new ParameterException("Syntax error: duplicate craft: " + value.toString());
				}
				if ("-=".equals(value.toString())) {
					throw new ParameterException("Syntax error: illegal craft name: " + value.toString());
				}
			}
		}).setAutoResolve(false);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String name = args.get("craft_name").toString();
		String craft = null;
		if (context.containsCraft(name)) {
			craft = context.getCraft(name);
		}
		craft = console.getTextInput(craft, name);
		context.setCraft(name, craft);
		console.addDynamicKey(name);
		return new XCLVar(name);
	}

}
