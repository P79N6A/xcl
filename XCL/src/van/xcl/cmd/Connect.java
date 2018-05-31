package van.xcl.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Connect implements Command {

	@Override
	public String name() {
		return "connect";
	}

	@Override
	public String description() {
		return "connect";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("ip");
		parameters.add("port", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				try {
					Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					throw new ParameterException("port should be a number!");
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String ip = args.get("ip").toString();
		int port = Integer.parseInt(args.get("port").toString());
		String target = ip + ":" + port;
		List<String> ignoredList = new ArrayList<String>();
		ignoredList.add("127.0.0.1" + ":" + console.getPort());
		ignoredList.add(CommonUtils.getLocalIPAddress() + ":" + console.getPort());
		if (ignoredList.contains(target)) {
			throw new CommandException("Can't connect to " + target);
		}
		console.connect(ip, port);
		return new XCLVar(target);
	}

}
