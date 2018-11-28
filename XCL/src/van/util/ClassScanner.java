package van.util;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

	private static final String CLASS_FILE_EXTENSION = ".class";
	private static final String JAR_FILE_EXTENSION = ".jar";
	
	public void scanClasses(List<Class<?>> classes) {
		long startInMillis = System.currentTimeMillis();
		String classpath = System.getProperty("java.class.path");
		String[] paths = classpath.split(";");
		for (String path : paths) {
			scanClass(path, path, classes);
		}
		System.out.println("Scan classes timeused: " + (System.currentTimeMillis() - startInMillis));
	}
	
	private void scanJarFile(JarFile jarFile, List<Class<?>> classes) {
		Enumeration<JarEntry> jarEntryEnum = jarFile.entries();
		while (jarEntryEnum.hasMoreElements()) {
			JarEntry entry = jarEntryEnum.nextElement();
			String jarEntryName = entry.getName();
			if (jarEntryName.contains(CLASS_FILE_EXTENSION)) {
				try {
					String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
					Class<?> clazz = Class.forName(className);
					classes.add(clazz);
				} catch (Throwable e) {
					System.out.println("scan jar class error: " + e.getMessage());
				}
			}
		}
	}
	
	private void scanClassFile(String dir, File file, List<Class<?>> classes) {
		String filename = file.getAbsolutePath();
		try {
			String packageSuffix = dir.replace("/", "\\").replaceFirst("\\\\", "");
			filename = filename.replace("/", "\\").replaceFirst("\\\\", "");
			filename = filename.replace(packageSuffix, "");
			filename = filename.replace("\\", ".");
			filename = filename.replace("/", ".");
			if (filename.startsWith(".")) {
				filename = filename.replaceFirst(".", "");
			}
			filename = filename.replace(CLASS_FILE_EXTENSION, "");
			Class<?> clazz = Class.forName(filename);
			classes.add(clazz);
		} catch (Throwable e) {
			System.out.println("scan file class error: " + e.getMessage());
		}
	}
	
	private void scanClass(String dir, String path, List<Class<?>> classes) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					scanClass(dir, f.getAbsolutePath(), classes);
				}
			} else {
				try {
					String filename = file.getAbsolutePath();
					if (filename.endsWith(JAR_FILE_EXTENSION)) {
						JarFile jarFile = new JarFile(file);
						scanJarFile(jarFile, classes);
					} else if (filename.endsWith(CLASS_FILE_EXTENSION)) {
						scanClassFile(dir, file, classes);
					} else {
						// Do nothing.
					}
				} catch (Throwable e) {
					System.out.println("scan class error: " + e.getMessage());
				}
			}
		}
	}
	
}
