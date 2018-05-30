package van.xcl;

import java.awt.Font;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class XCLStartup {
	
	private static Vector<XCLApplication> activeApps = new Vector<XCLApplication>();
	
	private static void initGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
	
	private static void initLookAndFeel() {
		try {
			initGlobalFont(XCLConstants.DEFAULT_FONT);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public static void shutdown(XCLApplication app, int status) {
		activeApps.remove(app);
		if (activeApps.isEmpty()) {
			System.exit(status);
		}
	}
	
	public static XCLApplication startup(XCLStartupParas paras) {
		XCLApplication app = new XCLApplication();
		activeApps.add(app);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				app.launch(paras);
			}
		});
		return app;
	}
	
	public static void main(String[] args) {
		initLookAndFeel();
		startup(new XCLStartupParas(args));
	}

}
