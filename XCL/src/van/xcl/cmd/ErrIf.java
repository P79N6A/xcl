package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class ErrIf implements Command {

	@Override
	public String name() {
		return "errif";
	}

	@Override
	public String description() {
		return "throw an error if the given boolean is true";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("boolean", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!value.isBoolean()) {
					throw new ParameterException("only allowed boolean: " + value.toString());
				}
			}
		});
		parameters.add("err");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		XCLVar bool = args.get("boolean");
		if (bool.getBoolean()) {
			throw new CommandException(args.get("err").toString());
		}
		return new XCLVar(bool);
	}

}