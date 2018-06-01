package van.xcl;

import java.util.HashMap;
import java.util.Map;

import van.util.CommonUtils;

public class XCLStartupParas {

	private Map<String, String> paras = new HashMap<String, String>();
	
	public XCLStartupParas(String[] args) {
		if (args != null && args.length > 0) {
			for (String arg : args) {
				String arr[] = arg.split(";");
				for (String str : arr) {
					parsePara(str);
				}
			}
		}
	}
	
	public XCLStartupParas() {
		
	}
	
	private void parsePara(String str) {
		String p[] = str.split("=");
		if (p.length == 2) {
			paras.put(CommonUtils.trim(p[0]), CommonUtils.trim(p[1]));
		}
	}
	
	public void setPara(String key, String value) {
		paras.put(key, value);
	}
	
	public String getPara(String key) {
		return paras.get(key);
	}
	
	public String getPara(String key, String defaultValue) {
		if (paras.containsKey(key)) {
			return paras.get(key);
		}
		return defaultValue;
	}
	
}
