package van.xcl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import java.util.Set;

public class XCLDynamicParameter {

	private static Logger logger = Logger.getLogger(XCLDynamicParameter.class);
	private Map<String, String> map = new HashMap<String, String>();
	
	public String getValue(String key) {
		return map.get(key);
	}
	
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public XCLDynamicParameter addDynamicParameterKeyValue(String str, XCLContext context) {
		if (isDynamicParameterKeyValue(str)) {
			String[] arr = splitKeyValue(str);
			if (arr != null) {
				map.put(arr[0].replaceFirst(XCLConstants.PARAS_PREFIX, "").trim(), context.resolveVar(arr[1].trim()));
			}
		}
		return this;
	}
	
	public XCLDynamicParameter addAll(XCLDynamicParameter mapping) {
		map.putAll(mapping.map);
		return this;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append(XCLConstants.PARAS_DELIMETER);
			}
			sb.append(XCLConstants.PARAS_PREFIX);
			sb.append(entry.getKey());
			sb.append(XCLConstants.PARAS_SPLITER);
			sb.append(entry.getValue());
		}
		return sb.toString();
	}
	
	public static XCLDynamicParameter resolveDynamicParameter(String string) {
		if (isDynamicParameter(string)) {
			XCLDynamicParameter mapping = new XCLDynamicParameter();
			String[] arr = string.split(XCLConstants.PARAS_DELIMETER);
			for (String str : arr) {
				String[] kv = splitKeyValue(str);
				if (kv != null) {
					mapping.map.put(kv[0].replaceFirst(XCLConstants.PARAS_PREFIX, "").trim(), kv[1].trim());
				}
			}
			return mapping;
		}
		return null;
	}
	
	public static boolean isDynamicParameter(String string) {
		return string != null ? string.startsWith(XCLConstants.PARAS_PREFIX) : false;
	}
	
	public static boolean isDynamicParameterKeyValue(String string) {
		return string != null ? (string.startsWith(XCLConstants.PARAS_PREFIX) && string.contains(XCLConstants.PARAS_SPLITER)) : false;
	}
	
	public static void validate(XCLVar var, String...requiredFields) throws ParameterException {
		if (!isDynamicParameter(var.toString())) {
			throw new ParameterException("Only dynamic parameter are allowed here.");
		}
		if (requiredFields != null && requiredFields.length > 0) {
			XCLDynamicParameter map = resolveDynamicParameter(var.toString());
			for (String field : requiredFields) {
				if (!map.containsKey(field)) {
					throw new ParameterException("The parameter \"" + field + "\" must be given.");
				}
			}
		}
	}
	
	private static String[] splitKeyValue(String str) {
		int idx = str.indexOf(XCLConstants.PARAS_SPLITER);
		if (idx > 0)  {
			String[] arr = new String[2];
			arr[0] = str.substring(0, idx);
			arr[1] = str.substring(idx + XCLConstants.PARAS_SPLITER.length());
			return arr;
		}
		return null;
	}
	
	public static void main(String[] args) {
		XCLDynamicParameter p = resolveDynamicParameter("-abcdsa=123213~-asdfasdfsdfsdfa=323323");
		logger.info(p.toString());
		
	}
	
}
