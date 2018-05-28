package van.util.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EventQueue {
	class Event {
		private EventType type;
		private String message;
		private EventCallback callback;
		public Event(EventType type, String message) {
			this.type = type;
			this.message = message;
		}
		public Event(EventType type, String message, EventCallback callback) {
			this.type = type;
			this.message = message;
			this.callback = callback;
		}
		public EventType getType() {
			return type;
		}
		public String getMessage() {
			return message;
		}
		public EventCallback getCallback() {
			return callback;
		}
	}
	
	class EventTask extends Thread {
		public EventTask() {
			setDaemon(true);
		}
		public void run() {
			try {
				while (isRunning.get()) {
					Event event = eventQueue.poll();
					if (event != null) {
						try {
							String result = handle(event.getType(), event.getMessage());
							if (event.getCallback() != null) {
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
	
	class EventThreadGroup extends ThreadGroup {
		public EventThreadGroup(EventGroup group) {
			super("EventGroup-" + group.getGroupName() + "-" + threadGroupCount.incrementAndGet());
		}
	}
	
	abstract class EventThread extends Thread {
		private CountDownLatch latch = null;
		public EventThread(CountDownLatch latch, EventType type) {
			super(new EventThreadGroup(type.getGroup()), "Event-" + type.getName() + "-" + threadCount.incrementAndGet());
			this.latch = latch;
			this.start();
		}
		public void run() {
			try {
				synchronized (threads) {
					threads.add(this);
				}
				onThread();
			} finally {
				synchronized (threads) {
					threads.remove(this);
				}
				latch.countDown();
			}
		}
		@SuppressWarnings("deprecation")
		public void stopThread() {
			latch.countDown();
			super.stop();
		}
		public abstract void onThread();
	}
	
	private EventHandler handler;
	private LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private EventTask task = new EventTask();
	private AtomicInteger threadGroupCount = new AtomicInteger();
	private AtomicInteger threadCount = new AtomicInteger();
	private Vector<EventThread> threads = new Vector<EventThread>();
	
	EventQueue(EventHandler handler) {
		this.handler = handler;
		this.isRunning.set(true);
		this.task.start();
	}
	
	void addEvent(EventType type, String message) {
		addEvent(type, message, null);
	}
	
	void addEvent(EventType type, String message, EventCallback callback) {
		Event e = new Event(type, message, callback);
		eventQueue.add(e);
		synchronized (this.task) {
			this.task.notifyAll();
		}
	}
	
	void shutdown() {
		if (this.isRunning.compareAndSet(true, false)) {
			synchronized (this.task) {
				this.task.notifyAll();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<String> stopAll() {
		List<String> threadNames = new ArrayList<String>();
		synchronized (threads) {
			for (EventThread t : threads) {
				EventThreadGroup threadGroup = (EventThreadGroup) t.getThreadGroup();
				Thread[] list = new Thread[threadGroup.activeCount()];
				t.getThreadGroup().enumerate(list);
				for (Thread tt : list) {
					if (tt instanceof EventThread) {
						((EventThread) tt).stopThread();
					} else {
						tt.stop();
					}
					threadNames.add("[" + threadGroup.getName() + "] - [" + tt.getName() + "]"); 
				}
			}
			threads.removeAllElements();
		}
		return threadNames;
	}
	
	private String handle(EventType type, String message) throws InterruptedException, ExecutionException {
		String[] result = {null};
		CountDownLatch latch = new CountDownLatch(1);
		new EventThread(latch, type) {
			@Override
			public void onThread() {
				result[0] = handler.handle(type, message);
			}
		};
		latch.await();
		return result[0];
	}

}
