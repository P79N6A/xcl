package van.xcl;

public interface ParameterValidator {
	public void validate(XCLContext context, XCLVar value) throws ParameterException;
}