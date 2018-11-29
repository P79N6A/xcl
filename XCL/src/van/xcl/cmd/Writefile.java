package van.xcl.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import van.util.CommonUtils;
import van.util.json.JsonArray;
import van.util.json.JsonObject;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Writefile implements Command {

	@Override
	public String name() {
		return "writefile";
	}

	@Override
	public String description() {
		return "write file";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("file", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				File file = new File(value.toString());
				checkFile(file);
			}
		});
		parameters.add("content");
		parameters.add("charset");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		try {
			String filepath = args.get("file").toString();
			String content = args.get("content").toString();
			String charset = args.get("charset").toString();
			File file = new File(filepath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter fw = new OutputStreamWriter(fos, charset);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(formatContent(content));
			bw.close();
			fw.close();
			return new XCLVar(filepath);
		} catch (IOException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	private String formatContent(String input) {
		XCLVar var = new XCLVar(input);
		if (var.isString() || var.isBoolean() || var.isNumeric()) {
			return CommonUtils.resolveJSONString(var.toString());
		} else if (var.isJsonObject()) {
			JsonObject obj = var.getJsonObject();
			return CommonUtils.resolveJSONString(JsonObject.toJSONString(obj, true));
		} else if (var.isJsonArray()) {
			JsonArray obj = var.getJsonArray();
			if (obj.size() > 0) {
				Object element = obj.get(0);
				if (element instanceof String) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0 ; i < obj.size() ; i++) {
						sb.append("\n" + (String) obj.get(i));
					}
					return sb.toString();
				} else {
					return CommonUtils.resolveJSONString(JsonObject.toJSONString(obj, true));
				}
			}
		}
		return input;
	}
	
	private void checkFile(File file) throws ParameterException {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent == null) {
				throw new ParameterException("Invalid file path.");
			}
			checkFile(parent);
		}
	}

}
