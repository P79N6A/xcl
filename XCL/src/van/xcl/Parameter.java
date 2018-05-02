package van.xcl;

public class Parameter {
	private String name;
	private ParameterValidator validator;
	private boolean isAutoResolve = true;
	public Parameter(String name, ParameterValidator validator) {
		this.name = name;
		this.validator = validator;
	}
	public void validate(XCLContext context, XCLVar value) throws ParameterException {
		this.validator.validate(context, value);
	}
	public boolean isAutoResolve() {
		return isAutoResolve;
	}
	public void setAutoResolve(boolean isAutoResolve) {
		this.isAutoResolve = isAutoResolve;
	}
	public String getName() {
		return name;
	}
	
}
