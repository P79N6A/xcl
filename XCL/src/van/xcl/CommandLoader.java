package van.xcl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import van.util.CommonUtils;

public class CommandLoader {
	public static final String propertiesFile = "cmd/cmd.properties";
	public static final String commandKey = "command-classes";
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Command>> loadCommandClasses() {
		try {
			List<Class<? extends Command>> classList = new ArrayList<Class<? extends Command>>();
			Properties p = new Properties();
			p.load(CommandLoader.class.getResourceAsStream(propertiesFile));
			String cmdClasses = p.getProperty(commandKey);
			if (!CommonUtils.isEmpty(cmdClasses)) {
				for (String cmdClass : cmdClasses.split(";")) {
					try {
						Class<?> clazz = Class.forName(cmdClass);
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
			return classList;
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "FAILED TO LOAD COMMANDS: " + CommonUtils.getStackTrace(e));
			throw new RuntimeException("FAILED TO LOAD COMMANDS");
		}
	}
}
