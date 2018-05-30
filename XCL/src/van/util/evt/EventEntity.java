package van.util.evt;

import java.io.Serializable;

import van.util.uuid.Unid;

public class EventEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2208784316122540513L;
	private EventType type;
	private String message;
	private EventCallback callback;
	private String uuid;
	public EventEntity(Unid uuid, EventType type, String message) {
		this.uuid = uuid.getUuid();
		this.type = type;
		this.message = message;
	}
	public EventEntity(Unid uuid, EventType type, String message, EventCallback callback) {
		this.uuid = uuid.getUuid();
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
	public boolean hasCallback() {
		return callback != null;
	}
	public String getUuid() {
		return uuid;
	}
}