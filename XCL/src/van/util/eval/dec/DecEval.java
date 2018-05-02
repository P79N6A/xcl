package van.util.eval.dec;

import java.math.BigDecimal;

import van.util.eval.Evaluator;

/**
 * Decimal Evaluator
 */
public interface DecEval extends Evaluator<BigDecimal> {
	public BigDecimal eval(Object input);
}
