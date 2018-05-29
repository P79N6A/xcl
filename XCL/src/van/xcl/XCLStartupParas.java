package van.xcl;

public class XCLStartupParas {

	private String[] args;
	public XCLStartupParas(String[] args) {
		this.args = args;
	}
	
	public String getStartup() {
		return this.args != null && this.args.length > 0 ? this.args[0] : null;
	}
	
	public String getContext() {
		return this.args != null && this.args.length > 1 ? this.args[1] : null;
	}
	
}
