package van.xcl.cmd;

import java.io.IOException;
import java.util.Map;

import van.util.CommonUtils;
import van.util.ZipUtils;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Unzip implements Command {

	@Override
	public String name() {
		return "unzip";
	}

	@Override
	public String description() {
		return "unzip";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("zipFilePath");
		parameters.add("unzipFilePath");
		parameters.add("includeZipFileName", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				if (!value.isBoolean()) {
					throw new ParameterException("Boolean is allowed.");
				}
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String zipFilePath = args.get("zipFilePath").toString();
		String unzipFilePath = args.get("unzipFilePath").toString();
		boolean includeZipFileName = args.get("includeZipFileName").getBoolean();
		try {
			ZipUtils.unzip(zipFilePath, unzipFilePath, includeZipFileName);
			return new XCLVar(Boolean.TRUE.toString());
		} catch (IOException e) {
			console.error(CommonUtils.getStackTrace(e));
		}
		return new XCLVar(Boolean.FALSE.toString());
	}

}
