package van.util;

public class StringJoiner {
	
	public static final String DEFAULT_DELIMETER = ",";
	
	private StringBuilder sb = new StringBuilder();
	
	private String delimeter;
	
	public StringJoiner(String delimeter) {
		this.delimeter = delimeter;
	}
	
	public StringJoiner() {
		this(DEFAULT_DELIMETER);
	}
	
	public void join(String str) {
		if (sb.length() > 0) {
			sb.append(delimeter);
		}
		sb.append(str);
	}
	
	public String toString() {
		return sb.toString();
	}

}
