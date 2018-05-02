package van.xcl.cmd;

import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Not implements Command {

	@Override
	public String name() {
		return "not";
	}

	@Override
	public String description() {
		return "not";
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
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		XCLVar v = new XCLVar(args.get("boolean").toString());
		return new XCLVar(!v.getBoolean());
	}

}
