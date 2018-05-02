package van.util.eval.bool;

import van.util.eval.Evaluator;

/**
 * Boolean Evaluator
 */
public interface BoolEval extends Evaluator<Boolean> {
	public Boolean eval(Object input);
	public void setNegative();
	public boolean isNegative();
}
