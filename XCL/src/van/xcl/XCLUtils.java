package van.xcl;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class XCLUtils {
	
	public static String defaulltFontPath = "font/YaheiConsolasHybrid.ttf";
	
	public static boolean isVarString(String str) {
		return (str.startsWith("\"") && str.endsWith("\""))
				|| (str.startsWith("'") && str.endsWith("'"));
	}

	public static String trimVar(String str) {
		if (str != null) {
			str = str.trim();
			if (str != null && isVarString(str)) {
				str = str.substring(1, str.length() - 1);
			}
			return str;
		}
		return null;
	}

	public static List<String> resolveParameters(String str) {
		str = captureQuota(str.trim());
		str = str.replace("\\\"", "{#}");
		StringBuilder newStr = new StringBuilder();
		int length = str.length();
		Map<String, String> map = new HashMap<String, String>();
		for (int idx = 0 , para = 0; idx < length ;) {
			int sIdx = str.indexOf("\"");
			int eIdx = str.indexOf("\"", sIdx + 1);
			if (sIdx == -1 && eIdx == -1) {
				newStr.append(str);
				idx += str.length();
			} else {
				newStr.append(str.substring(0, sIdx));
				idx += sIdx;
				String value = str.substring(sIdx, eIdx + 1);
				String key = "{" + (para++) + "}";
				map.put(key, value);
				newStr.append(key);
				idx += (eIdx - sIdx + 1);
				str = str.substring(eIdx + 1);
			}
		}
		List<String> list = new ArrayList<String>();
		Scanner sc = new Scanner(newStr.toString());
		while (sc.hasNext()) {
			String next = sc.next().trim();
			if (!"".equals(next)) {
				for (Entry<String, String> entry : map.entrySet()) {
					next = next.replace(entry.getKey(), entry.getValue());
				}
				list.add(trimVar(escapeQuota(next)));
			}
		}
		sc.close();
		return list;
	}
	
	public static List<List<String>> resolveCommands(String str) {
		str = trimCommand(str);
		str = captureQuota(str.trim());
		StringBuilder newStr = new StringBuilder();
		int length = str.length();
		Map<String, String> map = new HashMap<String, String>();
		for (int idx = 0 , para = 0; idx < length ;) {
			int sIdx = str.indexOf("\"");
			int eIdx = str.indexOf("\"", sIdx + 1);
			if (sIdx == -1 && eIdx == -1) {
				newStr.append(str);
				idx += str.length();
			} else if (eIdx > sIdx) {
				newStr.append(str.substring(0, sIdx));
				idx += sIdx;
				String value = str.substring(sIdx, eIdx + 1);
				String key = "{" + (para++) + "}";
				map.put(key, value);
				newStr.append(key);
				idx += (eIdx - sIdx + 1);
				str = str.substring(eIdx + 1);
			} else {
				throw new RuntimeException("Incomplete command line found: " + str);
			}
		}
		String[] newArr = newStr.toString().split(";");
		List<List<String>> cmdList = new ArrayList<List<String>>();
		for (String s : newArr) {
			for (Entry<String, String> entry : map.entrySet()) {
				s = s.replace(entry.getKey(), entry.getValue());
			}
			cmdList.add(resolveParameters(escapeQuota(s)));
		}
		return cmdList;
	}
	
	private static String trimCommand(String cmdString) {
		StringBuilder cmdTrim = new StringBuilder();
		if (cmdString != null) {
			try {
				BufferedReader br = new BufferedReader(new StringReader(cmdString));
				String cmdLine = null;
				while (null != (cmdLine = br.readLine())) {
					if (cmdLine.startsWith(Constants.COMMONT_PREFIX)) {
						continue;
					}
					cmdTrim.append(cmdLine + " ");
				}
				br.close();
			} catch (IOException e) {
				throw new RuntimeException("Unexpected IOException: " + e.getMessage());
			}
		}
		return cmdTrim.toString();
	}
	
	private static String captureQuota(String s) {
		s = s.replace("\\\"", "{#}");
		return s;
	}
	
	private static String escapeQuota(String s) {
		s = s.replace("{#}", "\"");
		return s;
	}
	
	public static Font getDefaultFont(int style, float size) {
		String filepath = XCLUtils.class.getResource(defaulltFontPath).getFile();
		return getSelfDefinedFont(filepath, style, size);
	}
	
	public static Font getSelfDefinedFont(String filepath, int style, float size) {
		Font font = null;
		File file = new File(filepath);
		try {
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream fb = new BufferedInputStream(fi);
			font = Font.createFont(Font.TRUETYPE_FONT, fb);
		} catch (FontFormatException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		font = font.deriveFont(style, size);
		return font;
	}
	
	public static void main(String[] args) {
		String s = "cmd asdfas \"{\\\"sadfsad\\\": \\\"asdfsad\\\"}\" asdfasd;\n asadfsadf asdfasd";
		System.out.println(resolveCommands(s));
	}
	
}
