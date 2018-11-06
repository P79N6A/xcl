package van.xcl;

import java.util.ArrayList;
import java.util.List;

import van.util.StringJoiner;

public class Parameter {
	private String name;
	private ParameterValidator validator;
	private boolean isAutoResolve = true;
	private List<String> paras = new ArrayList<String>();
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
	public Parameter addPara(String para) {
		paras.add(para);
		return this;
	}
	public String getParaForm() {
		StringBuilder s = new StringBuilder(name);
		if (paras.size() > 0) {
			s.append(":\n\t");
			StringJoiner sj = new StringJoiner("\n\t");
			for (String para : paras) {
				sj.join(para);
			}
			s.append(sj.toString());
		}
		return s.toString();
	}
	
}
