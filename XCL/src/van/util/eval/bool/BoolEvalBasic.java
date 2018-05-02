package van.util.eval.bool;

/**
 * Boolean Evaluator Basic
 */
public abstract class BoolEvalBasic implements BoolEval {
	private boolean isNegative = false;
	private String evalStr = null;
	protected void parse(String evalStr) {
		this.evalStr = evalStr.trim();
		parseImpl(this.evalStr);
	}
	protected String getevalStr() {
		return this.evalStr;
	}
	@Override
	public Boolean eval(Object o) {
		boolean ret = evalImpl(o);
		return isNegative ? !ret : ret;
	}
	@Override
	public void setNegative() {
		isNegative = true;
	}
	@Override
	public boolean isNegative() {
		return isNegative;
	}
	public abstract void parseImpl(String evalStr);
	public abstract boolean evalImpl(Object o);
}
