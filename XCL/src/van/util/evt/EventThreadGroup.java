package van.util.evt;

import java.util.concurrent.atomic.AtomicInteger;

public class EventThreadGroup extends ThreadGroup {
	private static AtomicInteger threadGroupCount = new AtomicInteger();
	public EventThreadGroup(EventGroup group) {
		super("EventGroup-" + group.getGroupName() + "-" + threadGroupCount.incrementAndGet());
	}
}
