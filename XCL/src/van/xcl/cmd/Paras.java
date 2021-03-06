package van.xcl.cmd;

import java.util.List;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.XCLConstants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.Resolver;
import van.xcl.XCLContext;
import van.xcl.XCLDynamicParameter;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Paras implements Command, Resolver {

	@Override
	public String name() {
		return XCLConstants.COMMAND_PARAS;
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
				XCLDynamicParameter.validate(value);
			}
		});
		parameters.add("append", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!XCLDynamicParameter.isDynamicParameterKeyValue(value.toString())) {
					throw new ParameterException("Syntax error: only key value is allowed here");
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		XCLDynamicParameter initial = XCLDynamicParameter.resolveDynamicParameter(args.get("initial").toString());
		initial.addDynamicParameterKeyValue(args.get("append").toString(), context);
		return new XCLVar(initial.toString());
	}

	@Override
	public void resolve(List<String> commands, XCLContext context) {
		int paraIndex = -1;
		int paraCount = 0;
		for (int i = 0 ; i < commands.size() ; i++) {
			String command = commands.get(i);
			if (XCLDynamicParameter.isDynamicParameterKeyValue(command)) {
				if (paraIndex == -1) {
					paraIndex = i;
				}
				paraCount++;
			} else if (isDefaultFlag(command)) {
				commands.set(i, XCLConstants.COMMAND_PARAS);
				commands.add(++i, defaultKeyValue(command));
				commands.add(++i, defaultKeyValue(command));
			} else {
				if (paraIndex != -1) {
					commands.add(paraIndex, defaultKeyValue(command));
					for (int j = 0 ; j < paraCount ; j++) {
						commands.add(paraIndex, XCLConstants.COMMAND_PARAS);
					}
					i += paraCount + 1;
					paraIndex = -1;
					paraCount = 0;
				}
			}
		}
		if (paraIndex != -1) {
			commands.add(paraIndex, defaultKeyValue(commands.get(paraIndex)));
			for (int j = 0 ; j < paraCount ; j++) {
				commands.add(paraIndex, XCLConstants.COMMAND_PARAS);
			}
			paraIndex = -1;
			paraCount = 0;
		}
	}
	
	public String defaultKeyValue(String cmd) {
		return XCLConstants.PARAS_PREFIX + XCLConstants.BUILTIN_VAL_PERFIX + XCLConstants.PARAS_SPLITER + cmd;
	}
	
	public boolean isDefaultFlag(String string) {
		return string != null && string.equals(XCLConstants.PARAS_DEFAULT);
	}

}
