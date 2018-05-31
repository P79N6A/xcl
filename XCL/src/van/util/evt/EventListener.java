package van.util.evt;

public interface EventListener {

	public void onEventAdded(EventEntity e);
	
	public void onEventTriggered(EventEntity e);
	
}
