package van.xcl.cmd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

public class FindStr implements Command {
	
	@Override
	public String name() {
		return "findstr";
	}
	
	@Override
	public String description() {
		return "find string";
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
		parameters.add("file_filter", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String filter = value.toString();
				StringFilter fileFilter = new StringFilter(filter);
				fileFilter.accept(""); // test
			}
		});
		parameters.add("content_filter", new ParameterValidator() {
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
		StringFilter fileFilter = new StringFilter(args.get("file_filter").toString());
		StringFilter contFilter = new StringFilter(args.get("content_filter").toString());
		File file = new File(path);
		if (!file.exists()) {
			console.error("file or directory is not exists: " + args.get("path").toString());
			return new XCLVar("");
		}
		AtomicInteger fileFound = new AtomicInteger(0);
		List<String> fileList = new ArrayList<String>();
		findFile(file, fileList, fileFilter, console, fileFound);
		List<String> contentList = new ArrayList<String>();
		AtomicInteger contentFound = new AtomicInteger(0);
		for (String filePath : fileList) {
			findContent(new File(filePath), contentList, contFilter, console, contentFound);
		}
		console.output("contents found: " + contentList.size());
		JsonArray arrar = new JsonArray();
		arrar.addAll(contentList);
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
	
	private void findContent(File file, List<String> contentList, StringFilter contentFilter, XCLConsole console, AtomicInteger count) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			int lineNum = 0;
			boolean isFirst = true;
			while (null != (line = br.readLine())) {
				lineNum++;
				if (contentFilter == null || contentFilter.accept(line)) {
					if (isFirst) {
						contentList.add(file.getAbsolutePath());
						isFirst = false;
					}
					contentList.add("\tline: " + lineNum + "\t" + line);
					console.prompt("records found: " + count.incrementAndGet());
				}
			}
		} catch (FileNotFoundException e) {
			console.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			console.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
