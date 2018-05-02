package van.util.eval.bool;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Boolean Evaluator Parser
 */
public class BoolEvalParser {
	private Class<? extends BoolEval> basicClass;
	public BoolEvalParser(Class<? extends BoolEval> basicClass) {
		this.basicClass = basicClass;
	}
	
	public BoolEval parse(String expStr) {
		return parse(expStr, false);
	}
	
	private BoolEval parse(String expStr, boolean neg) {
		String[] arr = split(expStr);
		int length = arr.length;
		if (length % 2 != 1) {
			throw new IllegalArgumentException("Invalid expression: " + expStr);
		}
		BoolEval rule = null;
		if (length == 1) {
			String word = arr[0];
			rule = parse1(word);
			if (neg) {
				rule.setNegative();
			}
		} else {
			String word1 = arr[0];
			BoolEval rule1 = parse1(word1);
			if (neg) {
				rule1.setNegative();
			}
			rule = new BoolEvalComplex(rule1);
			for (int i = 1 ; i < arr.length ; i+= 2) {
				String opr = arr[i]; // '&' or '|'
				String word2 = arr[i + 1];
				BoolEval rule2 = parse1(word2);
				if ("&".equals(opr)) {
					((BoolEvalComplex) rule).and(rule2);
				} else if ("|".equals(opr)) {
					((BoolEvalComplex) rule).or(rule2);
				} else {
					throw new IllegalArgumentException("A operator should appear here: " + opr);
				}
			}
		}
		return rule;
	}
	private BoolEval parse1(String word) {
		BoolEval rule = null;
		boolean neg = false;
		if (word.startsWith("!")) {
			neg = true;
			word = word.substring(1);
		}
		boolean bkt = brackets(word);
		while (brackets(word)) {
			word = word.substring(1, word.length() - 1);
		}
		if (isComplex(word)) {
			if (bkt & neg) {
				rule = parse(word, false);
				rule.setNegative();
			} else {
				rule = parse(word, neg);
			}
		} else {
			rule = newBasicObject(word);
			if (neg) {
				rule.setNegative();
			}
		}
		return rule;
	}
	private BoolEvalBasic newBasicObject(String word) {
		try {
			BoolEvalBasic matcherBasic;
			matcherBasic = (BoolEvalBasic) basicClass.newInstance();
			matcherBasic.parse(word);
			return matcherBasic;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unexpected InstantiationException: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unexpected IllegalAccessException: " + e.getMessage());
		}
	}
	private boolean isComplex(String word) {
		return word.length() >= 3 && (word.contains("|") || word.contains("&"));
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
				if (c == '|' || c == '&') {
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
