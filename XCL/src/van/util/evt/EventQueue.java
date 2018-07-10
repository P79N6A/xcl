package van.util.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventQueue {
	
	private EventHandler handler;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private Vector<EventTask<String>> activeTasks;
	private Queue<EventEntity> eventQueue;
	private EventConsumer eventConsumer;
	
	protected EventQueue(EventHandler handler) {
		this.handler = handler;
		this.isRunning.set(true);
		this.activeTasks = new Vector<EventTask<String>>();
		this.eventQueue = new LinkedBlockingQueue<EventEntity>();
		this.eventConsumer = new EventConsumer(this);
		this.eventConsumer.start();
	}
	
	protected Queue<EventEntity> getEventQueue() {
		return eventQueue;
	}
	
	protected void addEvent(EventSource source, EventType type, String message) {
		addEvent(source, type, message, null);
	}
	
	protected void addEvent(EventSource source, EventType type, String message, EventCallback callback) {
		EventEntity e = new EventEntity(source, type, message, callback);
		addEvent(e);
	}
	
	protected void addEvent(EventEntity e) {
		if (this.handler.prepareEvent(e)) {
			this.eventQueue.add(e);
			synchronized (this.eventConsumer) {
				this.eventConsumer.notifyAll();
			}
		}
	}
	
	protected void shutdown() {
		if (this.isRunning.compareAndSet(true, false)) {
			synchronized (this.eventConsumer) {
				this.eventConsumer.notifyAll();
			}
		}
	}
	
	protected List<String> stopAll() {
		List<String> threadNames = new ArrayList<String>();
		synchronized (activeTasks) {
			for (EventTask<?> t : activeTasks) {
				ThreadGroup threadGroup = t.getThreadGroup();
				Thread[] list = new Thread[threadGroup.activeCount()];
				t.getThreadGroup().enumerate(list);
				for (Thread tt : list) {
					if (tt instanceof EventTask) {
						((EventTask<?>) tt).stopThread();
					} else {
						EventTask.stopInternal(tt);
					}
					threadNames.add(getThreadName(tt)); 
				}
			}
			activeTasks.removeAllElements();
		}
		return threadNames;
	}
	
	protected String handle(EventEntity event) throws InterruptedException, ExecutionException {
		// we create a thread to handle the event to 
		// allow other to call EventQueue.stopAll() to interrupt the current operation
		EventTask<String> task = new EventTask<String>(event.getType()) {
			@Override
			public String onTask() {
				try {
					synchronized (activeTasks) {
						activeTasks.add(this);
					}
					// logger.info("EventQueue --> handle [source: " + event.getSource() + ", type: " + event.getType() + ", message: " + event.getMessage() + "]");
					return handler.handleEvent(event);
				} finally {
					synchronized (activeTasks) {
						activeTasks.remove(this);
					}
				}
			}
		};
		task.start();
		String result = task.waitResult();
		return result;
	}
	
	protected boolean isRunning() {
		return this.isRunning.get();
	}
	
	// ---------------- private methods
	
	private String getThreadName(Thread t) {
		String groupName = "[" + t.getThreadGroup().getName() + "]";
		String threadName = "[" + "Anonymous-" + t.getName() + "]";
		if (t instanceof EventTask) {
			threadName = "[" + t.getName() + "]";
		}
		return groupName + " - " + threadName;
	}
	
}
