package van.util.evt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EventTask<T> extends Thread {
	private static AtomicInteger threadCount = new AtomicInteger();
	private CountDownLatch latch = new CountDownLatch(1);
	private T result;
	public EventTask(EventType type) {
		super(new EventThreadGroup(type.getGroup()), "Event-" + type.getName() + "-" + threadCount.incrementAndGet());
	}
	public void run() {
		try {
			result = onTask();
		} finally {
			latch.countDown();
		}
	}
	public void stopThread() {
		latch.countDown();
		stopInternal(this);
	}
	public T waitResult() throws InterruptedException {
		latch.await();
		return result;
	}
	@SuppressWarnings("deprecation")
	public static void stopInternal(Thread t) {
		t.stop();
	}
	public abstract T onTask();
}
