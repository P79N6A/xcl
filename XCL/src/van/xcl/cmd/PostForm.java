package van.xcl.cmd;
import java.util.HashMap;
import java.util.Map;

import van.xcl.util.json.JsonObject;

import van.util.http.HttpClientService;
import van.util.http.HttpResult;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;

public class PostForm implements Command {
	private HttpClientService service = new HttpClientService();

	@Override
	public String name() {
		return "postf";
	}
	
	@Override
	public String description() {
		return "post form";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("url");
		parameters.add("body");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		try {
			String url = args.get("url").toString();
			String body = args.get("body").toString();
			console.prompt("Posting... [url: " + url + "]");
			Map<String, String> params = new HashMap<String, String>();
			for (String attrs : body.split("&")) {
				String[] attr = attrs.split("=");
				if (attr.length == 2) {
					params.put(attr[0], attr[1]);
				}
			}
			HttpResult httpResult = service.doPost(url, params);
			console.info(httpResult.toString());
			console.prompt("posted. [url: " + url + "]");
			try {
				JsonObject object = (JsonObject) JsonObject.parse(httpResult.getBody());
				return new XCLVar(object);
			} catch (Throwable e) {
				console.prompt("error: " + e.getMessage());
				console.info(httpResult.getBody());
				return new XCLVar(httpResult.getBody());
			}
		} catch (Exception e) {
			console.prompt("error: " + e.getMessage());
			console.error(e.getMessage());
			return new XCLVar();
		}
	}

}
