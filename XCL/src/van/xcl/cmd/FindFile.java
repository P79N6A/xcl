package van.xcl.cmd;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import van.util.json.JsonArray;
import van.util.sf.StringFilter;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class FindFile implements Command {
	
	@Override
	public String name() {
		return "findfile";
	}
	
	@Override
	public String description() {
		return "find file";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("path", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String path = value.toString();
				if (!new File(path).exists()) {
					throw new ParameterException("Cannot find the specified path: " + path);
				}
			}
		});
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
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String path = args.get("path").toString();
		StringFilter fileFilter = new StringFilter(args.get("filter").toString());
		File file = new File(path);
		if (!file.exists()) {
			console.error("file or directory is not exists: " + file.getAbsolutePath());
			return new XCLVar(path);
		}
		AtomicInteger fileFound = new AtomicInteger(0);
		List<String> fileList = new ArrayList<String>();
		findFile(file, fileList, fileFilter, console, fileFound);
		console.output("files found: " + fileList.size());
		JsonArray arrar = new JsonArray();
		arrar.addAll(fileList);
		return new XCLVar(arrar);
	}
	
	private void findFile(File file, List<String> fileList, StringFilter fileFilter, XCLConsole console, AtomicInteger count) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				findFile(f, fileList, fileFilter, console, count);
			}
		} else {
			if (fileFilter == null || fileFilter.accept(file.getName())) {
				fileList.add(file.getAbsolutePath());
				console.prompt("files found: " + count.incrementAndGet());
			}
		}
	}
	
}
