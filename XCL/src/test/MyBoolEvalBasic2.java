package test;

import van.util.eval.bool.BoolEvalBasic;

public class MyBoolEvalBasic2 extends BoolEvalBasic {

	boolean b = false;
	@Override
	public void parseImpl(String evalStr) {
		b = Boolean.valueOf(evalStr);
	}

	@Override
	public boolean evalImpl(Object o) {
		return b;
	}
	
}