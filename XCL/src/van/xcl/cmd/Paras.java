package van.xcl.cmd;

import java.util.List;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Constants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.Resolver;
import van.xcl.XCLContext;
import van.xcl.XCLParameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Paras implements Command, Resolver {

	@Override
	public String name() {
		return Constants.PARAS_COMMAND;
	}
	
	@Override
	public String description() {
		return "XCL paras";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("initial", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				XCLParameters.validate(value);
			}
		});
		parameters.add("append", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!XCLParameters.isKeyValue(value.toString())) {
					throw new ParameterException("Syntax error: key value is allowed");
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		XCLParameters initial = XCLParameters.resolveXCLParas(args.get("initial").toString());
		initial.addKeyValue(args.get("append").toString(), context);
		return new XCLVar(initial.toString());
	}

	@Override
	public void resolve(List<String> commands, XCLContext context) {
		int paraIndex = -1;
		int paraCount = 0;
		for (int i = 0 ; i < commands.size() ; i++) {
			String command = commands.get(i);
			if (XCLParameters.isKeyValue(command)) {
				if (paraIndex == -1) {
					paraIndex = i;
				}
				paraCount++;
			} else if (isDefaultFlag(command)) {
				commands.set(i, Constants.PARAS_COMMAND);
				commands.add(++i, defaultKeyValue());
				commands.add(++i, defaultKeyValue());
			} else {
				if (paraIndex != -1) {
					commands.add(paraIndex, defaultKeyValue());
					for (int j = 0 ; j < paraCount ; j++) {
						commands.add(paraIndex, Constants.PARAS_COMMAND);
					}
					i += paraCount + 1;
					paraIndex = -1;
					paraCount = 0;
				}
			}
		}
		if (paraIndex != -1) {
			commands.add(paraIndex, defaultKeyValue());
			for (int j = 0 ; j < paraCount ; j++) {
				commands.add(paraIndex, Constants.PARAS_COMMAND);
			}
			paraIndex = -1;
			paraCount = 0;
		}
	}
	
	public String defaultKeyValue() {
		return Constants.PARAS_PREFIX + "" + Constants.PARAS_SPLITER + "";
	}
	
	public boolean isDefaultFlag(String string) {
		return string != null && string.equals(Constants.PARAS_DEFAULT);
	}

}
