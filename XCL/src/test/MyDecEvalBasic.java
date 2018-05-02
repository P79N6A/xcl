package test;

import java.math.BigDecimal;
import java.util.Map;

import van.util.eval.dec.DecEvalBasic;

public class MyDecEvalBasic extends DecEvalBasic {
	private String key;
	public void parseImpl(String key) {
		this.key = key;
	}
	@Override
	public BigDecimal evalImpl(Object o) {
		@SuppressWarnings("unchecked")
		Map<String, BigDecimal> data = (Map<String, BigDecimal>)o;
		try {
			BigDecimal b = new BigDecimal(key);
			return b;
		} catch (NumberFormatException e) {
			// not a valid number, it should be defined in data map
			if (!data.containsKey(key)) {
				throw new IllegalArgumentException("Parameter " + key + " not set!");
			}
			return data.get(key);
		}
	}
}
