package van.xcl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import van.util.ClassScanner;
import van.util.CommonUtils;

public class CommandLoader {
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Command>> loadCommandClasses() {
		try {
			List<Class<? extends Command>> classList = new ArrayList<Class<? extends Command>>();
			List<Class<?>> classes = new ArrayList<Class<?>>();
			ClassScanner s = new ClassScanner();
			s.scanClasses(classes);
			for (Class<?> clazz : classes) {
				if (Command.class.isAssignableFrom(clazz)) {
					classList.add((Class<? extends Command>)clazz);
				}
			}
			return classList;
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "FAILED TO LOAD COMMANDS: " + CommonUtils.getStackTrace(e));
			throw new RuntimeException("FAILED TO LOAD COMMANDS");
		}
	}
}
