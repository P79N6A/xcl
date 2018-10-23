package van.xcl.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConstants;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLResult;
import van.xcl.XCLVar;

public class RunCraftFile implements Command {

	@Override
	public String name() {
		return XCLConstants.RUNFILE_COMMAND;
	}

	@Override
	public String description() {
		return "run craft file";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("file", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				File file = new File(value.toString());
				if (!file.exists() || !file.isFile()) {
					throw new ParameterException("File not found: " + file.getAbsolutePath());
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		File file = new File(args.get("file").toString());
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuilder cmd = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				cmd.append(line + "\r\n");
			}
			br.close();
			long startInMillis = System.currentTimeMillis();
			console.output("[" + file.getName() + "] craft file is started");
			XCLResult result = console.execute(cmd.toString(), context.clone());
			if (result.isSuccess()) {
				console.output("[" + file.getName() + "] craft file is done [Time Used: " + (System.currentTimeMillis() - startInMillis) + "ms]");
			}
			if (result.isSuccess()) {
				List<XCLVar> list = result.getResults();
				return list.get(list.size() - 1);
			}
			return new XCLVar();
		} catch (IOException e) {
			console.error(CommonUtils.getStackTrace(e));
		}
		return new XCLVar();
	}

}
