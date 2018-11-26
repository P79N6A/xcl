package van.xcl.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Readfile implements Command {

	@Override
	public String name() {
		return "readfile";
	}

	@Override
	public String description() {
		return "read file";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("file", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				File file = new File(value.toString());
				if (!file.exists()) {
					throw new ParameterException("The specified file is not exist");
				}
			}
		});
		parameters.add("var");
		parameters.add("charset");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		try {
			String filepath = args.get("file").toString();
			String charset = args.get("charset").toString();
			String varName = args.get("var").toString();
			FileInputStream file = new FileInputStream(filepath);
			InputStreamReader fr = new InputStreamReader(file, charset);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder content = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				content.append(line + "\r\n");
			}
			br.close();
			fr.close();
			context.setVar(varName, content.toString());
			return new XCLVar(varName);
		} catch (IOException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
}
