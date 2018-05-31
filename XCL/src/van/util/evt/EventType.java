package van.util.evt;

import java.io.Serializable;

public interface EventType extends Serializable {
	public EventGroup getGroup();
	public String getName();
}
