package van.xcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import van.util.evt.EventHandler;

public class XCLCmdParser {

	public class XCLNode {
		private String name;
		private String paraName;
		private XCLNode parent;
		private int depth;
		private boolean executable;
		private List<XCLNode> childs = new ArrayList<XCLNode>();
		public XCLNode(String name, XCLNode parent) {
			this.name = name;
			this.parent = parent;
			if (parent != null) {
				this.depth = parent.depth + 1;
			} else {
				this.depth = 1;
			}
		}
		public void addChild(XCLNode child) {
			this.childs.add(child);
		}
		public List<XCLNode> getChilds() {
			return childs;
		}
		public boolean hasChilds() {
			return childs.size() > 0;
		}
		public XCLNode getParent() {
			return parent;
		}
		public String getName() {
			return name;
		}
		public int getDepth() {
			return depth;
		}
		public String getParaName() {
			return paraName;
		}
		public void setParaName(String paraName) {
			this.paraName = paraName;
		}
		public String toString() {
			if (childs.size() > 0) {
				return name + ": " + childs.toString() + " ";
			} else {
				return name;
			}
		}
		public void setExecutable(boolean executable) {
			this.executable = executable;
		}
		public boolean isExecutable() {
			return executable;
		}
		public XCLVar execute(XCLConsole console, XCLContext context, XCLCmdHolder holder, EventHandler handler) throws ParameterException, CommandException {
			if (this.executable) {
				Command command = holder.getCommand(name);
				List<Parameter> paraList = command.parameters().list();
				Map<String, XCLVar> args = new HashMap<String, XCLVar>();
				if (childs.size() == paraList.size()) {
					for (int i = 0 ; i < childs.size() ; i++) {
						XCLNode child = childs.get(i);
						Parameter para = paraList.get(i);
						XCLVar childVar = child.execute(console, context, holder, handler);
						if (!childVar.isNull()) {
							if (para.isAutoResolve()) {
								childVar = new XCLVar(context.resolveVar(childVar.toString()));
							}
							para.validate(context, childVar);
							args.put(child.getParaName(), childVar);
						} else {
							throw new ParameterException("The command '" + name + "' returns an empty object. [para: " + child.getParaName() + "]");
						}
					}
					String argstr = "";
					if (args.size() > 0) {
						argstr = args.toString();
					}
					if (!name.equals(XCLConstants.PARAS_COMMAND)) {
						console.info(getGap(getDepth(), "-") + " " + name + " " + argstr);
					}
					return command.execute(this, args, console, context);
				} else {
					StringBuilder usage = new StringBuilder("syntax error\n");
					usage.append("    usage:\n");
					usage.append("        " + command.name());
					for (Parameter para : command.parameters().list()) {
						usage.append(" [" + para.getName() + "]");
					}
					throw new ParameterException(usage.toString());
				}
			} else {
				return new XCLVar(name);
			}
		}
		public String getFormatString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getGap(getDepth(), "  ") + name + "\n");
			if (hasChilds()) {
				for (XCLNode child : childs) {
					sb.append("  " + child.getFormatString());
				}
			}
			return sb.toString();
		}
		private String getGap(int depth, String flag) {
			StringBuilder gap = new StringBuilder();
			for (int i = 0 ; i < depth ; i++) {
				gap.append(flag);
			}
			return gap.toString();
		}
	}
	
	private XCLNode parseNode(XCLNode par, List<String> paras, Parameter parameter, XCLContext context, XCLCmdHolder holder) {
		String value = getHead(paras);
		if (value != null) {
			XCLNode node = new XCLNode(value, par);
			if (parameter != null) {
				node.setParaName(parameter.getName());
			}
			if (holder.isCommand(value)) {
				for (Parameter para : holder.getCommand(value).parameters().list()) {
					parseNode(node, paras, para, context, holder);
				}
				node.setExecutable(true);
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
	
	private String getHead(List<String> paras) {
		if (paras != null && paras.size() > 0) {
			String value = paras.get(0);
			paras.remove(0);
			return value;
		}
		return null;
	}
	
	public XCLNode parseCommand(List<String> command, XCLContext context, XCLCmdHolder holder) {
		for (Resolver resolver : holder.allResolvers()) {
			resolver.resolve(command, context);
		}
		XCLNode node = parseNode(null, command, null, context, holder);
		return node;
	}
	
	
}
