package test;

import van.util.eval.bool.BoolEval;
import van.util.eval.bool.BoolEvalParser;

public class BoolEvalTester {

	public static void main(String[] args) {

//		BoolEvalParser mp = new BoolEvalParser(MyBoolEvalBasic.class);
//		BoolEval exp2 = mp.parse("a* | b* & *c & x ");
//		System.out.println(exp2.eval("asadf"));
//		System.out.println(exp2.eval("bsdfdfc"));
//		System.out.println(exp2.eval("bsdfxdfc"));
//		System.out.println(exp2.eval("xxx"));
//		System.out.println(exp2.eval("yyy"));
		BoolEvalParser mp = new BoolEvalParser(MyBoolEvalBasic2.class);
		BoolEval exp2 = mp.parse("(false|(true&true))");
		System.out.println(exp2.eval(null));
	}

}
