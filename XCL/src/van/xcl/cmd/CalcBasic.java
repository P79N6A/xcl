package van.xcl.cmd;

import java.math.BigDecimal;

import van.util.eval.dec.DecEvalBasic;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class CalcBasic extends DecEvalBasic {
	private String key;
	@Override
	public void parseImpl(String key) {
		this.key = key;
	}
	@Override
	public BigDecimal evalImpl(Object o) {
		XCLContext context = (XCLContext) o;
		XCLVar var = context.getXCLVar(key);
		if (var != null && var.isString()) {
			try {
				return new BigDecimal(var.getString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Parameter " + key + " is not numeric!");
			}
		} else {
			try {
				return new BigDecimal(key);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Parameter " + key + " is not numeric!");
			}
		}
	}
}