package van.xcl;

import java.awt.Font;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import van.util.CommonUtils;

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
			JOptionPane.showMessageDialog(null, CommonUtils.getStackTrace(e));
		}
	}
	
	private static void initShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (!activeApps.isEmpty()) {
					System.out.println("ShutdownHook - terminated!");
					// terminate(-1);
				} else {
					System.out.println("ShutdownHook - normal shutdown.");
				}
			}
		});
	}
	
	public static void shutdown(XCLApplication app, int status) {
		app.shutdown();
		activeApps.remove(app);
		if (activeApps.isEmpty()) {
			System.exit(status);
		}
	}
	
	public static void terminate(int status) {
		if (!activeApps.isEmpty()) {
			for (XCLApplication app : activeApps) {
				app.shutdown();
			}
			activeApps.clear();
		}
	}
	
	public static XCLApplication startup(XCLStartupParas paras) {
		XCLApplication app = new XCLApplication();
		activeApps.add(app);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				app.startup(paras);
			}
		});
		return app;
	}
	
	public static void main(String[] args) {
		initLookAndFeel();
		initShutdownHook();
		startup(new XCLStartupParas(args));
	}

}
