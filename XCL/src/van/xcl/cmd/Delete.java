package van.xcl.cmd;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.util.sf.StringFilter;
import van.xcl.XCLVar;

public class Delete implements Command {
	
	@Override
	public String name() {
		return "delete";
	}
	
	@Override
	public String description() {
		return "delete files";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("path");
		parameters.add("filter", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String filter = value.toString();
				StringFilter fileFilter = new StringFilter(filter);
				fileFilter.accept(""); // test
			}
		});
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String filepath = args.get("path").toString();
		StringFilter fileFilter = new StringFilter(args.get("filter").toString());
		AtomicInteger count = new AtomicInteger(0);
		File file = new File(filepath);
		if (file.exists()) {
			console.output("Delete --> " + filepath);
			delete(context, file, fileFilter, count, console);
			console.prompt("Total files deleted: " + count.get());
		} else {
			console.output("\"" + filepath + "\" is not exists.");
		}
		return new XCLVar();
	}
	
	private void delete(XCLContext context, File file, StringFilter sf, AtomicInteger count, XCLConsole console) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					delete(context, f, sf, count, console);
				}
				file.delete();
			} else {
				if (sf == null || sf.accept(file.getName())) {
					file.delete();
					console.prompt("Files are being deleted. [" + count.incrementAndGet() + "]");
				}
			}
		}
	}

}
