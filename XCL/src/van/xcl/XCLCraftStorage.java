package van.xcl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class XCLCraftStorage {
	
	private File getCraftPath() {
		return new File(XCLConstants.CRAFT_FILE_PATH);
	}
	
	private File getCraftFile(String craft) {
		return new File(getCraftPath(), craft + XCLConstants.CRAFT_FILE_EXT);
	}

	public Map<String, String> getCrafts() {
		Map<String, String> crafts = new HashMap<String, String>();
		File path = getCraftPath();
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				String name = file.getName().substring(0, file.getName().lastIndexOf("."));
				String craft = getCraft(name);
				crafts.put(name, craft);
			}
		}
		return crafts;
	}
	
	public void setCraft(String name, String craft) {
		File cf = getCraftFile(name);
		BufferedWriter bw = null;
		try {
			ensureFile(cf, false);
			bw = new BufferedWriter(new FileWriter(cf));
			bw.append(craft);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "FAILED TO SAVE THE CRAFT - [craft: " + name + ", error: " + e.getMessage() + "]");
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getCraft(String name) {
		StringWriter sw = new StringWriter();
		File cf = getCraftFile(name);
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(cf));
			bw = new BufferedWriter(sw);
			String line = null;
			while (null != (line = br.readLine())) {
				bw.write(line);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sw.toString().trim();
	}
	
	public boolean removeCraft(String name) {
		return getCraftFile(name).delete();
	}
	
	public boolean containsCraft(String name) {
		return getCraftFile(name).exists();
	}
	
	private void ensureFile(File file, boolean isFolder) throws IOException {
		if (!file.exists()) {
			if (isFolder) {
				file.mkdirs();
			} else {
				File parent = file.getParentFile();
				ensureFile(parent, true);
				file.createNewFile();
			}
		}
	}
	
}
