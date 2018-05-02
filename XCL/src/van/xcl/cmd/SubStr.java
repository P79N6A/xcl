package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class SubStr implements Command {

	@Override
	public String name() {
		return "substr";
	}

	@Override
	public String description() {
		return "sub string";
	}

	@Override
	public Parameters parameters() {
		ParameterValidator numberValidator = new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!value.isNumeric()) {
					throw new ParameterException("only number is allowed: " + value.toString());
				}
			}
		};
		Parameters parameters = new Parameters();
		parameters.add("string");
		parameters.add("beginIndex", numberValidator);
		parameters.add("endIndex", numberValidator);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String string = args.get("string").toString();
		int beginIndex = args.get("beginIndex").getNumber().intValue();
		int endIndex = args.get("endIndex").getNumber().intValue();
		String substr = string.substring(beginIndex, endIndex);
		return new XCLVar(substr);
	}

}
