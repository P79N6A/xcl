package van.util.evt;

import java.util.concurrent.CountDownLatch;

public abstract class EventCallback {
	
	private CountDownLatch latch = new CountDownLatch(1);
	private String result = null;
	
	final void handleResult(String result) {
		this.latch.countDown();
		this.result = result;
		callback(result);
	}
	
	public final String awaitResult() {
		try {
			this.latch.await();
		} catch(InterruptedException e) {
			throw new RuntimeException("Unexpected InterruptedException: " + e.getMessage(), e);
		}
		return result;
	}
	
	public abstract void callback(String result);
	
	public static EventCallback defaultCallback() {
		return new EventCallback() {
			@Override
			public void callback(String result) {
			}
		};
	}
	
}
