package van.xcl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JOptionPane;

import van.util.CommonUtils;

public class CommandLoader {
	public static final String propertiesFile = "cmd/cmd.properties";
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Command>> loadCommandClasses() {
		try {
			List<Class<? extends Command>> classList = new ArrayList<Class<? extends Command>>();
			Properties p = new Properties();
			p.load(CommandLoader.class.getResourceAsStream(propertiesFile));
			for (Entry<Object, Object> e : p.entrySet()) {
				String key = String.valueOf(e.getKey());
				String cmdClass = String.valueOf(e.getValue());
				System.out.println(key + "=" + cmdClass);
				try {
					Class<?> clazz = Class.forName(cmdClass);
					if (Command.class.isAssignableFrom(clazz)) {
						classList.add((Class<? extends Command>)clazz);
					}
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				} catch (ClassCastException ex) {
					ex.printStackTrace();
				}
			}
			return classList;
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "FAILED TO LOAD COMMANDS: " + CommonUtils.getStackTrace(e));
			throw new RuntimeException("FAILED TO LOAD COMMANDS");
		}
	}
}
