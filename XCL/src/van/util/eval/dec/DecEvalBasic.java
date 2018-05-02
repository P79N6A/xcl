package van.util.eval.dec;

import java.math.BigDecimal;

/**
 * Decimal Evaluator Basic
 */
public abstract class DecEvalBasic implements DecEval {
	private String evalStr = null;
	protected void parse(String evalStr) {
		this.evalStr = evalStr.trim();
		parseImpl(this.evalStr);
	}
	@Override
	public BigDecimal eval(Object data) {
		return evalImpl(data);
	}
	public abstract BigDecimal evalImpl(Object data);
	public abstract void parseImpl(String evalStr);
}
