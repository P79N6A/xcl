package van.xcl;

import java.util.Timer;
import java.util.TimerTask;

public class XCLHealthChecker {
	
	public interface XCLHealthEntity {
		public void onHealthCheck();
	}
	
	private static XCLHealthChecker instance;
	private Timer timer = new Timer();
	
	private XCLHealthChecker() {
	}
	
	public static XCLHealthChecker getChecker() {
		if (instance == null) {
			synchronized (XCLHealthChecker.class) {
				if (instance == null) {
					instance = new XCLHealthChecker();
				}
			}
		}
		return instance;
	}
	
	public void register(XCLHealthEntity entity) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				entity.onHealthCheck();
			}
		};
		timer.schedule(task, XCLConstants.HEALTH_CHECK_PERIOD, XCLConstants.HEALTH_CHECK_PERIOD);
	}
	
	public void shutdown() {
		timer.cancel();
	}
	
}
