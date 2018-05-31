package van.util.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskService {
	
	private static TaskService instance;
	
	public static TaskService getService() {
		if (instance == null) {
			synchronized(TaskService.class) {
				if (instance == null) {
					instance = new TaskService();
				}
			}
		}
		return instance;
	}
	
	private ExecutorService service;
	private ThreadFactory threadFactory;
	private static AtomicInteger count = new AtomicInteger(0);
	
	private TaskService() {
	}
	
	private ThreadFactory getThreadFactory(String name) {
		if (threadFactory == null) {
			threadFactory = new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setDaemon(true);
					t.setName(name + "-" + count.incrementAndGet());
					return t;
				}
			};
		}
		return threadFactory;
	}
	
	public void init(String name, int threads) {
		service = Executors.newFixedThreadPool(threads, getThreadFactory(name));
	}
	
	public void runTask(Task task) {
		Runnable t = new Runnable() {
			public void run() {
				task.run();
			}
		};
		service.submit(t);
	}
	
	public void shutdown() {
		if (service != null) {
			service.shutdown();
		}
	}

}
