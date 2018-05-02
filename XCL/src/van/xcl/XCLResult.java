package van.xcl;

import java.util.ArrayList;
import java.util.List;

public class XCLResult {
	private boolean isSuccess = false;
	private List<XCLVar> results = new ArrayList<XCLVar>();
	public void addResult(XCLVar var) {
		results.add(var);
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public List<XCLVar> getResults() {
		return results;
	}
	public boolean isSuccess() {
		return isSuccess;
	}

}
