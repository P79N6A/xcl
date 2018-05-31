package van.util.evt;

public interface EventHandler {

	public String handleEvent(EventEntity e);
	
	public boolean prepareEvent(EventEntity e);
	
}
