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

public class Sleep implements Command {

	@Override
	public String name() {
		return "sleep";
	}

	@Override
	public String description() {
		return "sleep";
	}
	

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("timeout", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!value.isNumeric()) {
					throw new ParameterException("timeout should be a numeric");
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		long timeout = args.get("timeout").getNumber().longValue();
		try {
			console.info("sleep " + timeout + "ms");
			long startMillis = System.currentTimeMillis();
			long elapsed = 0L;
			for (; elapsed < timeout ;) {
				Thread.sleep(1000L);
				elapsed = System.currentTimeMillis() - startMillis;
				console.info((elapsed / 1000) + "s is elapsed");
			}
		} catch (InterruptedException e) {
			return new XCLVar(Boolean.FALSE.toString());
		}
		return new XCLVar(Boolean.TRUE.toString());
	}

}
