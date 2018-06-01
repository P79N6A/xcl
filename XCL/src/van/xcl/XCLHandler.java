package van.xcl;

import java.util.List;

public interface XCLHandler {
	public void startup(XCLStartupParas paras);
	public XCLVar command(List<String> command, XCLConsole console, XCLContext context);
	public boolean isCommand(String key);
	public boolean isScript(String key);
	public void shutdown();
	public boolean switchContext(String name);
	public String currentContext();
	public XCLContext getContext();
}
