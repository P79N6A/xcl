package van.util.eval.dec;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Decimal Evaluator Parser
 */
public class DecEvalParser {
	
	private Class<? extends DecEvalBasic> basicClass;
	
	public DecEvalParser(Class<? extends DecEvalBasic> basicClass) {
		this.basicClass = basicClass;
	}
	
	public DecEval parse(String expStr) {
		String[] arr = split(expStr);
		int length = arr.length;
		if (length % 2 != 1) {
			throw new IllegalArgumentException("Invalid expression: " + expStr);
		}
		DecEval calc = null;
		if (length == 1) {
			String word = arr[0];
			calc = parse1(word);
		} else {
			String word1 = arr[0];
			DecEval calc1 = parse1(word1);
			calc = new DecEvalComplex(calc1);
			for (int i = 1 ; i < arr.length ; i+= 2) {
				String opr = arr[i]; // '&' or '|'
				String word2 = arr[i + 1];
				DecEval calc2 = parse1(word2);
				if ("+".equals(opr)) {
					((DecEvalComplex) calc).add(calc2);
				} else if ("-".equals(opr)) {
					((DecEvalComplex) calc).subtract(calc2);
				} else if ("*".equals(opr)) {
					((DecEvalComplex) calc).multiply(calc2);
				} else if ("/".equals(opr)) {
					((DecEvalComplex) calc).divide(calc2);
				} else {
					throw new IllegalArgumentException("A operator should appear here: " + opr);
				}
			}
		}
		return calc;
	}
	private DecEval parse1(String word) {
		DecEval rule = null;
		while (brackets(word)) {
			word = word.substring(1, word.length() - 1);
		}
		if (isComplex(word)) {
			rule = parse(word);
		} else {
			rule = newBasicObject(word);
		}
		return rule;
	}
	private DecEvalBasic newBasicObject(String word) {
		try {
			DecEvalBasic formulaBasic;
			formulaBasic = (DecEvalBasic) basicClass.newInstance();
			formulaBasic.parse(word);
			return formulaBasic;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unexpected InstantiationException: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unexpected IllegalAccessException: " + e.getMessage());
		}
	}
	private boolean isComplex(String word) {
		return word.length() >= 3 && (word.contains("+") || word.contains("-") || word.contains("*") || word.contains("/"));
	}
	private String scan(String s) {
		Scanner sc = new Scanner(s);
		StringBuilder sb = new StringBuilder();
		while(sc.hasNext()) {
			sb.append(sc.next());
		}
		sc.close();
		return sb.toString();
	}
	private String[] split(String s) {
		s = scan(s);
		char[] arr = s.toCharArray();
		List<String> list = new ArrayList<String>();
		StringBuilder wsb = new StringBuilder();
		int depth = 0;
		for (int i = 0 ; i < arr.length ; i++) {
			char c = arr[i];
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
			}
			if (depth == 0) {
				if (c == '+' || c == '-' || c == '*' || c == '/') {
					String wd = wsb.toString();
					wsb = new StringBuilder();
					list.add(wd);
					list.add(String.valueOf(c));
				} else {
					wsb.append(c);
				}
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
	private boolean brackets(String word) {
		if (word.startsWith("(") && word.endsWith(")")) {
			char[] arr = word.toCharArray();
			int depth = 0;
			int index = -1;
			for (int i = 0 ; i < arr.length ; i++) {
				char c = arr[i];
				if (c == '(') {
					depth++;
				} else if (c == ')') {
					depth--;
				}
				if (depth == 0) {
					index = i;
				}
			}
			if (index == arr.length - 1) {
				return true;
			}
		}
		return false;
	}
}
