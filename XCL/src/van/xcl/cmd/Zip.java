package van.xcl.cmd;

import java.io.IOException;
import java.util.Map;

import van.util.CommonUtils;
import van.util.ZipUtils;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Zip implements Command {

	@Override
	public String name() {
		return "zip";
	}

	@Override
	public String description() {
		return "zip";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("srcPath");
		parameters.add("zipPath");
		parameters.add("zipFileName");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String srcPath = args.get("srcPath").toString();
		String zipPath = args.get("zipPath").toString();
		String zipFileName = args.get("zipFileName").toString();
		try {
			ZipUtils.zip(srcPath, zipPath, zipFileName);
			return new XCLVar(Boolean.TRUE.toString());
		} catch (IOException e) {
			console.error(CommonUtils.getStackTrace(e));
		}
		return new XCLVar(Boolean.FALSE.toString());
	}

}
