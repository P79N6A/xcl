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

public class If implements Command {

	@Override
	public String name() {
		return "if";
	}
	
	@Override
	public String description() {
		return "if [boolean] then [ok] else [not ok]";
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
		parameters.add("then");
		parameters.add("else");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		boolean bool = args.get("boolean").getBoolean();
		if (bool) {
			return new XCLVar(args.get("then").toString());
		} else {
			return new XCLVar(args.get("else").toString());
		}
	}

}
