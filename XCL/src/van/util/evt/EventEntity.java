package van.util.evt;

import java.io.Serializable;

public class EventEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2208784316122540513L;
	private EventType type;
	private String message;
	private EventCallback callback;
	private String source;
	private int traceId;
	public EventEntity(EventSource source, EventType type, String message, int traceId, EventCallback callback) {
		this.source = source.getSource();
		this.type = type;
		this.message = message;
		this.traceId = traceId;
		this.callback = callback;
	}
	public EventType getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	public int getTraceId() {
		return traceId;
	}
	public EventCallback getCallback() {
		return callback;
	}
	public boolean hasCallback() {
		return callback != null;
	}
	public String getSource() {
		return source;
	}
	public String toString() {
		return "source=" + source + ",type=" + type.getGroup() + "." + type.getName() + ",message=" + message + ",callback=" + callback;
	}
	
}