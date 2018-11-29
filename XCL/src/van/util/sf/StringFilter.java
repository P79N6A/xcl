package van.util.sf;

import java.util.ArrayList;
import java.util.List;

import van.util.eval.bool.BoolEval;
import van.util.eval.bool.BoolEvalParser;
import van.xcl.XCLUtils;

public class StringFilter {
	
	private BoolEvalParser parser = new BoolEvalParser(StringFilterBasic.class);
	private List<BoolEval> evals = new ArrayList<BoolEval>();
	
	public StringFilter(String filterString) {
		filterString = XCLUtils.trimVar(filterString);
		if (filterString != null && !"*".equals(filterString)) {
			for (String str : filterString.split(",")) {
				if (str != null && !"".equals(str.trim())) {
					BoolEval be = parser.parse(str);
					evals.add(be);
				}
			}
		}
	}
	
	public boolean accept(String str) {
		if (evals.size() > 0) {
			for (BoolEval eval : evals) {
				if (eval.eval(str)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

}
