package van.xcl.cmd;

import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Vars implements Command {

	@Override
	public String name() {
		return "vars";
	}
	
	@Override
	public String description() {
		return "show all variables";
	}

	@Override
	public Parameters parameters() {
		return new Parameters();
	}
	
	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		StringBuilder sb = new StringBuilder("\n\n");
		for (Entry<String, Object> entry : context.getDataMap().entrySet()) {
			sb.append("\t");
			sb.append(entry.getKey());
			sb.append("\t=\t");
			Object obj = entry.getValue();
			if (obj instanceof JSONObject) {
				sb.append("[JSONObject]");
			} else if (obj instanceof JSONArray) {
				sb.append("[JSONArray]");
			} else {
				 sb.append(String.valueOf(obj));
			}
			sb.append("\n");
		}
		console.output(sb.toString());
		return new XCLVar();
	}
	
}
