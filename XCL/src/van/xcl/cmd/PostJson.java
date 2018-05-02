package van.xcl.cmd;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import van.util.http.HttpClientService;
import van.util.http.HttpResult;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class PostJson implements Command {
	private HttpClientService service = new HttpClientService();
	
	@Override
	public String name() {
		return "postj";
	}
	
	@Override
	public String description() {
		return "post json";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("url");
		parameters.add("body");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		try {
			String url = args.get("url").toString();
			String body = args.get("body").toString();
			console.prompt("Posting... [url: " + url + "]");
			HttpResult httpResult = service.doPostJson(url, body);
			console.prompt("posted. [url: " + url + "]");
			
			try {
				JSONObject object = (JSONObject) JSONObject.parse(httpResult.getBody());
				return new XCLVar(object);
			} catch (Exception e) {
				console.error(e.getMessage());
				return new XCLVar();
			}
		} catch (Exception e) {
			console.error(e.getMessage());
			return new XCLVar();
		}
	}
	
}
