package van.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import van.util.Base64.Decoder;
import van.util.Base64.Encoder;
import van.xcl.util.json.Json;
import van.xcl.util.json.JsonObject;
import van.xcl.util.sf.StringFilter;

public class CommonUtils {
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getCurrentTimeString() {
		return sdf.format(new Date());
	}
	
	public static String getCurrentDateString() {
		return sdf2.format(new Date());
	}
	
	public static String getStackTrace(Throwable e) {
		if (e instanceof ThreadDeath) {
			return "[ThreadDeath]";
		} else {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(bao));
			return bao.toString();
		}
    }
	
	public static String resolveString(String str, int maxlen) {
		if (str != null && str.length() > maxlen) {
			try {
				StringBuilder s = new StringBuilder();
				ByteArrayInputStream bos = new ByteArrayInputStream(str.getBytes("utf-8"));
				BufferedReader br = new BufferedReader(new InputStreamReader(bos));
				String line = null;
				while (null != (line = br.readLine())) {
					if (line.length() > maxlen) {
						line = line.substring(0, maxlen) + "...";
					}
					s.append(line + "\n");
				}
				br.close();
				bos.close();
				return s.toString().trim();
			} catch (Throwable e) {
				// do nothing
			}
		}
		return str;
	}
	
	public static String filter(String src, String key) {
		if (src != null) {
			char[] srcArr = src.toCharArray();
			int[] marks = new int[srcArr.length];
			StringBuilder sb = new StringBuilder();
			for (int i = 0, j = 0 ; i < srcArr.length ; i++) {
				char c = srcArr[i];
				if (c < 0 || c > 32) {
					sb.append(Character.toLowerCase(c));
					marks[j] = i;
					j++;
				}
			}
			String trims = sb.toString();
			String target = key.toLowerCase(); // converts key to lower case
			int keyLength = target.length();
			int pos = 0;
			int[] skips = new int[marks.length]; // change to use array to improve the performance
			for (int i = 0 ; i < skips.length ; i++) {
				skips[i] = -1; // initial -1
			}
			int index = -1;
			int fromIndex = 0;
			while (-1 < (index = trims.indexOf(target, fromIndex))) { // finding key ...
				if (index > -1) {
					for (int i = index ; i < index + keyLength; i++) {
						skips[pos++] = marks[i];
					}
					fromIndex = index + keyLength;
				}
			}
			char[] desArr = new char[srcArr.length]; // use char array directly
			filter: for (int i = 0 ; i < srcArr.length ; i++) {
				for (int j = 0 ; j < pos ; j++) {
					if (skips[j] == i) {
						desArr[i] = '*';
						continue filter;
					}
				}
				desArr[i] = srcArr[i];
			}
			return new String(desArr);
		}
		return src;
	}

	public static String encodeBase64(String value) {
		try {
			Encoder encoder = Base64.getEncoder();
			String result = new String(encoder.encode(value.getBytes("utf-8")), "utf-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected UnsupportedEncodingException: " + e.getMessage(), e);
		}
	}

	public static String decodeBase64(String value) {
		try {
			Decoder decoder = Base64.getDecoder();
			String result = new String(decoder.decode(value.getBytes("utf-8")), "utf-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected UnsupportedEncodingException: " + e.getMessage(), e);
		}
	}

	public static String resolveJSONString(String str) {
		try {
			if (str != null) {
				Json json = (Json) JsonObject.parse(str);
				String prettyString = JsonObject.toJSONString(json, true);
				prettyString = prettyString.replace("\t", "    ");
				return prettyString;
			}
		} catch (Exception e) {

		}
		return str;
	}

	public static String resolveAsOneRow(String text) {
		StringBuilder line = new StringBuilder();
		BufferedReader br = new BufferedReader(new StringReader(text));
		String content = null;
		try {
			while (null != (content = br.readLine())) {
				line.append(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line.toString();
	}

	public static boolean isBasicObject(Object obj) {
		if (obj instanceof Integer
				|| obj instanceof Double
				|| obj instanceof Float
				|| obj instanceof Long
				|| obj instanceof Boolean
				|| obj instanceof String
				) {
			return true;
		}
		return false;
	}

	public static Object parseJsonText(String text) {
		if (text != null) {
			text = text.trim();
			if (text.startsWith("{") && text.endsWith("}")) {
				return JsonObject.parseObject(text);
			} else if (text.startsWith("[") && text.endsWith("]")) {
				return JsonObject.parseArray(text);
			} else {
				return text;
			}
		}
		return null;
	}

	public static void scanClass(File baseFile, File file, List<String> files, StringFilter filter) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				scanClass(baseFile, f, files, filter);
			}
		} else {
			String filePath = file.getAbsolutePath();
			if (filePath.endsWith(".class")) {
				String className = filePath.substring(0,  filePath.indexOf(".class"));
				className = className.replace(baseFile.getAbsolutePath(), "");
				className = className.replace(File.separator, ".");
				className = className.substring(1);
				if (filter.accept(className)) {
					files.add(className);
				}
			}
		}
	}

	public static List<String> listFiles(File rootFile, List<String> fileList) throws IOException {
		File[] allFiles = rootFile.listFiles();
		for (File file : allFiles) {
			if (file.isDirectory()) {
				listFiles(file, fileList);
			} else {
				String path = file.getCanonicalPath();
				String clazz = path.substring(path.indexOf("classes") + 8);
				fileList.add(clazz.replace("//", ".").substring(0, clazz.lastIndexOf(".")));
			}
		}
		return fileList;
	}

	public static boolean isMatch(String regex, String orginal){ 
		if (orginal == null || orginal.trim().equals("")) { 
			return false; 
		} 
		Pattern pattern = Pattern.compile(regex); 
		Matcher isNum = pattern.matcher(orginal); 
		return isNum.matches(); 
	} 

	public static boolean isPositiveInteger(String orginal) { 
		return isMatch("^\\+{0,1}[1-9]\\d*", orginal); 
	} 

	public static boolean isNegativeInteger(String orginal) { 
		return isMatch("^-[1-9]\\d*", orginal); 
	} 

	public static boolean isWholeNumber(String orginal) { 
		return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal); 
	} 

	public static boolean isPositiveDecimal(String orginal){ 
		return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal); 
	} 

	public static boolean isNegativeDecimal(String orginal){ 
		return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal); 
	} 

	public static boolean isDecimal(String orginal){ 
		return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal); 
	} 

	public static boolean isRealNumber(String orginal){ 
		return isWholeNumber(orginal) || isDecimal(orginal); 
	}

	public static boolean isBoolean(String value) {
		return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	
	public static String trim(String str) {
		return str == null ? "" : str.trim();
	}
	
	public static String readFileToString(File file, String encode) throws IOException {
		StringBuffer buffer = new StringBuffer();
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), encode);
		BufferedReader ins = new BufferedReader(read);
		String line = null;
		while (null != (line = ins.readLine())) {
			buffer.append(line + "\n");
		}
		ins.close();
		return buffer.toString();
	}
	
	public static final String getLocalIPAddress() {
		try {
			String IPString = "";
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
						return ip.getHostAddress();
					}
				}
			}
			return IPString;
		} catch (SocketException e) {
			return "N/A";
		}
    }
	
}
