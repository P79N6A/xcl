package van.util.sf;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import van.util.eval.bool.BoolEvalBasic;

public class StringFilterBasic extends BoolEvalBasic {
	private String expStr = null;
	private String exp1;
	private String exp2;
	private int type = -1;
	
	public void parseImpl(String expStr) {
		this.expStr = expStr.trim();
		int idx = this.expStr.indexOf("*");
		if (idx == -1) {
			type = 1;
			exp1 = this.expStr;
		} else {
			int lastIdx = this.expStr.lastIndexOf("*");
			if (lastIdx == (this.expStr.length() - 1)) {
				type = 1;
				exp1 = this.expStr.substring(1, this.expStr.length() - 1);
			} else {
				if (idx == 0) {
					type = 2;
					exp1 = this.expStr.substring(1);
				} else if (idx == this.expStr.length() - 1) {
					type = 3;
					exp1 = this.expStr.substring(0, this.expStr.length() - 1);
				} else {
					type = 4;
					exp1 = this.expStr.substring(0, idx);
					exp2 = this.expStr.substring(idx + 1);
				}
			}
		}
	}
	
	private List<String> scan(String s) {
		List<String> list = new ArrayList<String>();
		Scanner sc = new Scanner(s);
		while(sc.hasNext()) {
			list.add(sc.next());
		}
		sc.close();
		return list;
	}
	public boolean evalImpl(Object s) {
		List<String> list = scan(String.valueOf(s));
		for (String w : list) {
			switch (type) {
			case 1 : if (w.contains(exp1)) return true; break;
			case 2 : if (w.endsWith(exp1)) return true; break;
			case 3 : if (w.startsWith(exp1)) return true; break;
			case 4 : if (w.startsWith(exp1) && w.endsWith(exp2)) return true; break;
			}
		}
		return false;
	}
	
}