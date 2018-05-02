package test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import van.util.eval.dec.DecEval;
import van.util.eval.dec.DecEvalParser;

public class DecEvalTester {

	public static void main(String[] args) {
		String s1 = "A * B - (C + D / E) * B ";
		String s2 = "35 + 1 * 2 + (3 /5 - 8) ";
		
		DecEvalParser p = new DecEvalParser(MyDecEvalBasic.class);
		DecEval ee1 = p.parse(s1);
		Map<String, BigDecimal> m = new HashMap<String, BigDecimal>();
		m.put("A", new BigDecimal("3"));
		m.put("B", new BigDecimal("5"));
		m.put("C", new BigDecimal("2"));
		m.put("D", new BigDecimal("3"));
		m.put("E", new BigDecimal("2"));
		System.out.println(s1 + "=" + ee1.eval(m));
		
		
		DecEvalParser p2 = new DecEvalParser(MyDecEvalBasic.class);
		DecEval ee2 = p2.parse(s2);
		System.out.println(s2 + "=" + ee2.eval(m));
	}
}
