package van.util.evt;

public class EventConsumer extends Thread {
	private EventQueue eventQueue;
	public EventConsumer(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
		setDaemon(true);
	}
	public void run() {
		try {
			while (eventQueue.isRunning()) {
				EventEntity event = eventQueue.getEventQueue().poll();
				if (event != null) {
					try {
						String result = eventQueue.handle(event);
						
						if (event.hasCallback()) {
							event.getCallback().handleResult(result);
						}
					} catch (Throwable e) {
						// Do nothing
					}
					continue;
				}
				synchronized (this) {
					this.wait();
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Unexpected InterruptedException: " + e.getMessage());
		}
	}
}
