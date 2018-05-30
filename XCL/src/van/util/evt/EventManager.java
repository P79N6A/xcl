package van.util.evt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import van.util.uuid.Unid;

public class EventManager {
	
	private Map<EventGroup, EventQueue> queueMap = new HashMap<EventGroup, EventQueue>();
	
	public void register(EventGroup group, EventHandler handler) {
		if (!queueMap.containsKey(group)) {
			EventQueue queue = new EventQueue(handler);
			queueMap.put(group, queue);
			System.out.println("event group is registered: " + group.getGroupName());
		}
	}
	
	public List<String> stopAll(EventGroup group) {
		if (queueMap.containsKey(group)) {
			return queueMap.get(group).stopAll();
		}
		return new ArrayList<String>();
	}
	
	public void addEvent(Unid token, EventType type, String message) {
		addEvent(token, type, message, null);
	}
	
	public void addEvent(Unid token, EventType type, String message, EventCallback callback) {
		if (queueMap.containsKey(type.getGroup())) {
			queueMap.get(type.getGroup()).addEvent(token, type, message, callback);
		}
	}
	
	public void shutdown() {
		for (Entry<EventGroup, EventQueue> entry : queueMap.entrySet()) {
			entry.getValue().shutdown();
		}
	}

}
