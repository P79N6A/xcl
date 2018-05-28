package van.xcl;

import van.util.evt.EventGroup;

public enum XCLEventGroup implements EventGroup {
	cmd,
	ui;
	public String getGroupName() {
		return this.name();
	}

}
