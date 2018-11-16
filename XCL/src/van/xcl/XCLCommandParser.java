package van.xcl;

import java.util.List;

public class XCLCommandParser {

	private XCLCommandNode parseNode(XCLCommandNode par, List<String> paras, Parameter parameter, XCLContext context, XCLCommandHolder holder) {
		String value = takeNode(paras);
		if (value != null) {
			XCLCommandNode node = new XCLCommandNode(value, par);
			if (parameter != null) {
				node.setParaName(parameter.getName());
			}
			if (holder.isCommand(value)) {
				node.setExecutable(true);
				for (Parameter para : holder.getCommand(value).parameters().list()) {
					parseNode(node, paras, para, context, holder);
				}
			} else {
				node.setExecutable(false);
			}
			if (par != null) {
				par.addChild(node);
			}
			return node;
		}
		return null;
	}
	
	private String takeNode(List<String> paras) {
		if (paras != null && paras.size() > 0) {
			String value = paras.get(0);
			paras.remove(0);
			return value;
		}
		return null;
	}
	
	public XCLCommandNode parseCommand(List<String> command, XCLContext context, XCLCommandHolder holder) {
		for (Resolver resolver : holder.allResolvers()) {
			resolver.resolve(command, context);
		}
		XCLCommandNode node = parseNode(null, command, null, context, holder);
		return node;
	}
	
}
