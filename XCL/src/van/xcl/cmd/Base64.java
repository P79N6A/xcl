package van.xcl.cmd;
import java.util.Map;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class Base64 implements Command {
	
	@Override
	public String name() {
		return "b64";
	}
	
	@Override
	public String description() {
		return "base64 encoder/decoder";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("-d/-e", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String v = value.toString();
				if (!"-d".equals(v) && !"-e".equals(v) ) {
					throw new ParameterException("Optional value: -d or -e");
				}
			}
		});
		parameters.add("content");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String type = args.get("-d/-e").toString();
		String text = args.get("content").toString();
		try {
			String result = null;
			if ("-e".equals(type)) {
				result = encode(text, console);
				return new XCLVar(result);
			} else if ("-d".equals(type)) {
				result = decode(text, console);
				return new XCLVar(result);
			}
		} catch (Exception e) {
			console.error("Unexpected Exception: " + e.getMessage());
		}
		return new XCLVar();
	}

	private String encode(String value, XCLConsole console) {
		console.info(value);
		String result = CommonUtils.encodeBase64(value);
		//console.output("encoded string:\n" + result);
		return result;
	}
	
	private String decode(String value, XCLConsole console) {
		console.info(value);
		String result = CommonUtils.decodeBase64(value);
		// console.output("decoded string:\n" + CommonUtils.resolveJSONString(result));
		return result;
	}
	
}
