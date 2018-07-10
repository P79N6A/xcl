package van.util.evt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class EventManager {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private Map<String, EventQueue> queueMap = new HashMap<String, EventQueue>();
	
	public void register(EventGroup group, EventHandler handler) {
		if (!queueMap.containsKey(group)) {
			EventQueue queue = new EventQueue(handler);
			queueMap.put(group.getGroupName(), queue);
			logger.info("event group is registered: " + group.getGroupName());
		}
	}
	
	public List<String> stopAll(EventGroup group) {
		if (queueMap.containsKey(group.getGroupName())) {
			return queueMap.get(group.getGroupName()).stopAll();
		}
		return new ArrayList<String>();
	}
	
	public void addEvent(EventEntity event) {
		if (event != null) {
			String groupName = event.getType().getGroup().getGroupName();
			if (queueMap.containsKey(groupName)) {
				queueMap.get(groupName).addEvent(event);
			}
		}
	}
	
	public void addEvent(EventSource source, EventType type, String message) {
		addEvent(source, type, message, null);
	}
	
	public void addEvent(EventSource source, EventType type, String message, EventCallback callback) {
		String groupName = type.getGroup().getGroupName();
		if (queueMap.containsKey(groupName)) {
			queueMap.get(groupName).addEvent(source, type, message, callback);
		}
	}
	
	public void shutdown() {
		for (Entry<String, EventQueue> entry : queueMap.entrySet()) {
			entry.getValue().shutdown();
		}
	}

}
