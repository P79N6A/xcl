package van.util.eval.bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Boolean Evaluator Complex
 */
public class BoolEvalComplex implements BoolEval {
	private boolean isNegative = false;
	private HashMap<Integer, BoolEval> rules = new HashMap<Integer, BoolEval>();
	private String cals = null;
	private List<List<BoolEval>> ruleHeap = new ArrayList<List<BoolEval>>();
	private boolean isComplied = false;
	public BoolEvalComplex(BoolEval rule) {
		rules.put(rule.hashCode(), rule);
		cals = String.valueOf(rule.hashCode());
	}

	public BoolEvalComplex and(BoolEval rule) {
		rules.put(rule.hashCode(), rule);
		cals += "&" + rule.hashCode();
		return this;
	}

	public BoolEvalComplex or(BoolEval rule) {
		rules.put(rule.hashCode(), rule);
		cals += "|" + rule.hashCode();
		return this;
	}

	@Override
	public Boolean eval(Object o) {
		boolean ret = false;
		if (!isComplied) { // compiling
			String[] orArr = cals.split("\\|");
			for (int i = 0 ; i < orArr.length ; i++) {
				String orExp = orArr[i];
				if (orExp != null && !"".equals(orExp.trim())) {
					String[] andArr = orExp.split("&");
					List<BoolEval> list = new ArrayList<BoolEval>();
					for (int j = 0 ; j < andArr.length ; j++) {
						String andExp = andArr[j];
						if (andExp != null && !"".equals(andExp.trim())) {
							Integer key = Integer.parseInt(andExp);
							BoolEval rule = rules.get(key);
							list.add(rule);
						}
					}
					ruleHeap.add(list);
				}
			}
			isComplied = true;
		}
		// accept
		for (int i = 0 ; i < ruleHeap.size() ; i++) {
			boolean b = true;
			List<BoolEval> list = ruleHeap.get(i);
			for (int j = 0 ; j < list.size() ; j++) {
				BoolEval rule = list.get(j);
				b = b & rule.eval(o);
			}
			if (b) {
				ret = true;
				break;
			}
		}
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

}
