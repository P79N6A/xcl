package van.xcl;

import java.util.ArrayList;
import java.util.List;

public class Parameters {
	
	private List<Parameter> paraList = new ArrayList<Parameter>();
	
	public int size() {
		return paraList.size();
	}
	
	public List<Parameter> list() {
		return paraList;
	}
	
	public Parameter add(String name) {
		return this.add(name, new ParameterValidator() {
			public void validate(XCLContext context, XCLVar value) throws ParameterException {}
		});
	}
	
	public Parameter add(String name, ParameterValidator validator) {
		Parameter parameter = new Parameter(name, validator);
		paraList.add(parameter);
		return parameter;
	}
	
}
