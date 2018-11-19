package van.xcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import van.util.evt.EventHandler;

public class XCLCommandNode {
	private String name;
	private String paraName;
	private XCLCommandNode parent;
	private int depth;
	private boolean executable;
	private List<XCLCommandNode> children = new ArrayList<XCLCommandNode>();
	public XCLCommandNode(String name, XCLCommandNode parent) {
		this.name = name;
		this.parent = parent;
		if (parent != null) {
			this.depth = parent.depth + 1;
		} else {
			this.depth = 1;
		}
	}
	public void addChild(XCLCommandNode child) {
		this.children.add(child);
	}
	public List<XCLCommandNode> getChildren() {
		return children;
	}
	public boolean hasChildren() {
		return children.size() > 0;
	}
	public XCLCommandNode getParent() {
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
		if (children.size() > 0) {
			return name + ": " + children.toString() + " ";
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
	public XCLVar execute(XCLConsole console, XCLContext context, XCLCommandHolder holder, EventHandler handler) throws ParameterException, CommandException {
		if (this.executable) {
			Command command = holder.getCommand(name);
			List<Parameter> paraList = command.parameters().list();
			Map<String, XCLVar> args = new HashMap<String, XCLVar>();
			if (children.size() == paraList.size()) {
				for (int i = 0 ; i < children.size() ; i++) {
					XCLCommandNode child = children.get(i);
					Parameter para = paraList.get(i);
					XCLVar childVar = child.execute(console, context, holder, handler);
					if (!childVar.isNull()) {
						if (para.isAutoResolve()) {
							childVar = new XCLVar(context.resolveVar(childVar.toString()));
						}
						para.validate(context, childVar);
						args.put(child.getParaName(), childVar);
					} else {
						throw new ParameterException("The child command \"" + child.getName() + "\" does not return any results for the parent command \"" + name + "\". [parameter: " + child.getParaName() + "]");
					}
				}
				String argstr = "";
				if (args.size() > 0) {
					argstr = args.toString();
				}
				String execInfo = getPadSpecific(getDepth(), "-") + " " + name + " " + argstr;
				console.info(execInfo);
				return command.execute(this, args, console, context);
			} else {
				StringBuilder usage = new StringBuilder("syntax error\n");
				usage.append("    usage:\n");
				usage.append("        " + command.name());
				for (Parameter para : command.parameters().list()) {
					usage.append(" <" + para.getParaForm() + ">");
				}
				throw new ParameterException(usage.toString());
			}
		} else {
			return new XCLVar(name);
		}
	}
	public String getFormatString() {
		String pad = "  ";
		StringBuilder sb = new StringBuilder();
		sb.append(getPadSpecific(getDepth(), pad) + name);
		sb.append("\n");
		if (hasChildren()) {
			for (XCLCommandNode child : children) {
				sb.append(pad + child.getFormatString());
			}
		}
		return sb.toString();
	}
	private String getPadSpecific(int depth, String flag) {
		StringBuilder gap = new StringBuilder();
		for (int i = 0 ; i < depth ; i++) {
			gap.append(flag);
		}
		return gap.toString();
	}
}