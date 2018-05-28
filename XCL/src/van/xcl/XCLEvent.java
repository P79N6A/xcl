package van.xcl;

import van.util.evt.EventType;

public enum XCLEvent implements EventType {
	run(XCLEventGroup.cmd),
	prepare(XCLEventGroup.ui),
	input(XCLEventGroup.ui),
	output(XCLEventGroup.ui),
	info(XCLEventGroup.ui),
	error(XCLEventGroup.ui),
	prompt(XCLEventGroup.ui),
	title(XCLEventGroup.ui),
	clear(XCLEventGroup.ui),
	editable(XCLEventGroup.ui),
	present(XCLEventGroup.ui),
	textTitle(XCLEventGroup.ui),
	textInput(XCLEventGroup.ui),
	getTextInput(XCLEventGroup.ui);
	private XCLEventGroup group;
	private XCLEvent(XCLEventGroup group) {
		this.group = group;
	}
	public XCLEventGroup getGroup() {
		return this.group;
	}
	public String getName() {
		return this.name();
	}
}
