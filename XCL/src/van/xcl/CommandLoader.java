package van.xcl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import van.util.CommonUtils;

public class CommandLoader {
	
	public static final String packageName = "van.xcl.cmd";

	@SuppressWarnings("unchecked")
	public static List<Class<? extends Command>> loadCommandClasses() {
		try {
			String path = CommandLoader.class.getResource("cmd").getFile();
			List<Class<? extends Command>> classList = new ArrayList<Class<? extends Command>>();
			int jarIndex = path.indexOf("!");
			if (jarIndex > 0) { // load from jar folder
				String jarFilePath = path.substring(0, jarIndex);
				JarFile jarFile = null;
				try {
					jarFile = new JarFile(new File(new URI(jarFilePath)));
					Enumeration<JarEntry> entries = jarFile.entries();
					List<String> list = new ArrayList<String>();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						name = name.replace("/", ".").replace("\\", ".");
						int index = name.indexOf(packageName);
						if (name.endsWith(".class") && index > -1) {
							String className = name.substring(index, name.length() - 6);
							list.add(className);
							try {
								Class<?> clazz = Class.forName(className);
								if (Command.class.isAssignableFrom(clazz)) {
									classList.add((Class<? extends Command>)clazz);
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (ClassCastException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e));
				} catch (URISyntaxException e) {
					JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e));
				} finally {
					try {
						jarFile.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e1));
					}
				}
			} else { // load from bin folder
				File file = new File(path);
				if (file.exists()) {
					for (String name : file.list()) {
						if (name.endsWith(".class")) {
							name = name.substring(0, name.length() - 6);
							String className = packageName + "." + name;
							try {
								Class<?> clazz = Class.forName(className);
								if (Command.class.isAssignableFrom(clazz)) {
									classList.add((Class<? extends Command>)clazz);
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (ClassCastException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			return classList;
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "FAILED TO LOAD COMMANDS: " + CommonUtils.getStackTrace(e));
			throw new RuntimeException("FAILED TO LOAD COMMANDS");
		}
	}
}
