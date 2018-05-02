package van.util.eval.dec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Decimal Evaluator Complex
 */
public class DecEvalComplex implements DecEval {
	private HashMap<Integer, DecEval> evalMap = new HashMap<Integer, DecEval>();
	private String evalStr = null;
	public DecEvalComplex(DecEval rule) {
		evalMap.put(rule.hashCode(), rule);
		evalStr = String.valueOf(rule.hashCode());
	}

	public DecEvalComplex add(DecEval rule) {
		evalMap.put(rule.hashCode(), rule);
		evalStr += "+" + rule.hashCode();
		return this;
	}

	public DecEvalComplex subtract(DecEval rule) {
		evalMap.put(rule.hashCode(), rule);
		evalStr += "-" + rule.hashCode();
		return this;
	}
	
	public DecEvalComplex multiply(DecEval rule) {
		evalMap.put(rule.hashCode(), rule);
		evalStr += "*" + rule.hashCode();
		return this;
	}
	
	public DecEvalComplex divide(DecEval rule) {
		evalMap.put(rule.hashCode(), rule);
		evalStr += "/" + rule.hashCode();
		return this;
	}

	@Override
	public BigDecimal eval(Object data) {
		// +
		// -
		String[] arr = split0(evalStr);
		if (arr.length % 2 != 1) {
			throw new IllegalArgumentException("Invalid expression!");
		}
		if (arr.length == 1) {
			String key = arr[0];
			return evaluate0(key, data);
		}
		String word = arr[0];
		BigDecimal ret = evaluate0(word, data);
		for (int i = 1 ; i < arr.length ; i+=2) {
			String opr = arr[i];
			String csr = arr[i + 1];
			BigDecimal val = evaluate0(csr, data);
			if ("+".equals(opr)) {
				ret = ret.add(val);
			} else if ("-".equals(opr)) {
				ret = ret.subtract(val);
			} else {
				throw new IllegalArgumentException("A operator should appear here: " + opr);
			}
		}
		return ret;
	}
	
	private BigDecimal evaluate0(String cals, Object data) {
		// *
		// /
		String[] arr = split1(cals);
		if (arr.length % 2 != 1) {
			throw new IllegalArgumentException("Invalid expression!");
		}
		if (arr.length == 1) {
			String key = arr[0];
			return evalMap.get(Integer.parseInt(key)).eval(data);
		}
		String word = arr[0];
		BigDecimal ret = evalMap.get(Integer.parseInt(word)).eval(data);
		for (int i = 1 ; i < arr.length ; i+=2) {
			String opr = arr[i];
			String csr = arr[i + 1];
			BigDecimal val = evalMap.get(Integer.parseInt(csr)).eval(data);
			if ("*".equals(opr)) {
				ret = ret.multiply(val);
			} else if ("/".equals(opr)) {
				try {
					ret = ret.divide(val);
				} catch (ArithmeticException e) { // fixed Non-terminating decimal expansion
					ret = ret.divide(val, 32, RoundingMode.HALF_UP);
				}
			} else {
				throw new IllegalArgumentException("A operator should appear here: " + opr);
			}
		}
		return ret;
	}
	
	private String[] split0(String calc) {
		char[] arr = calc.toCharArray();
		List<String> list = new ArrayList<String>();
		StringBuilder wsb = new StringBuilder();
		int depth = 0;
		for (int i = 0 ; i < arr.length ; i++) {
			char c = arr[i];
			if (c == '+' || c == '-') {
				String wd = wsb.toString();
				wsb = new StringBuilder();
				list.add(wd);
				list.add(String.valueOf(c));
			} else {
				wsb.append(c);
			}
		}
		if (depth == 0) {
			String wd = wsb.toString();
			if (wd.length() > 0) {
				list.add(wd);
			}
		}
		for (int i = 0 ; i < list.size() ; i++) {
			String w = list.get(i);
			if (w == null || "".equals(w.trim())) {
				list.remove(i);
			}
		}
		return list.toArray(new String[0]);
	}
	
	private String[] split1(String calc) {
		char[] arr = calc.toCharArray();
		List<String> list = new ArrayList<String>();
		StringBuilder wsb = new StringBuilder();
		int depth = 0;
		for (int i = 0 ; i < arr.length ; i++) {
			char c = arr[i];
			if (c == '*' || c == '/') {
				String wd = wsb.toString();
				wsb = new StringBuilder();
				list.add(wd);
				list.add(String.valueOf(c));
			} else {
				wsb.append(c);
			}
		}
		if (depth == 0) {
			String wd = wsb.toString();
			if (wd.length() > 0) {
				list.add(wd);
			}
		}
		for (int i = 0 ; i < list.size() ; i++) {
			String w = list.get(i);
			if (w == null || "".equals(w.trim())) {
				list.remove(i);
			}
		}
		return list.toArray(new String[0]);
	}

}
