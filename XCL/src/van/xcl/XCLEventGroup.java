package van.xcl;

import van.util.evt.EventGroup;

public enum XCLEventGroup implements EventGroup {
	CMD_EVENT,
	SYNC_UI_EVENT,
	ASYNC_UI_EVENT;
	public String getGroupName() {
		return this.name();
	}

}
