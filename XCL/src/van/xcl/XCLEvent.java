package van.xcl;

import van.util.evt.EventType;

public enum XCLEvent implements EventType {
	run(XCLEventGroup.CMD_EVENT),
	prepare(XCLEventGroup.SYNC_UI_EVENT),
	input(XCLEventGroup.SYNC_UI_EVENT),
	output(XCLEventGroup.SYNC_UI_EVENT),
	info(XCLEventGroup.SYNC_UI_EVENT),
	error(XCLEventGroup.SYNC_UI_EVENT),
	prompt(XCLEventGroup.SYNC_UI_EVENT),
	title(XCLEventGroup.SYNC_UI_EVENT),
	clear(XCLEventGroup.SYNC_UI_EVENT),
	editable(XCLEventGroup.SYNC_UI_EVENT),
	present(XCLEventGroup.SYNC_UI_EVENT),
	fixedRow(XCLEventGroup.SYNC_UI_EVENT),
	textTitle(XCLEventGroup.SYNC_UI_EVENT),
	textInput(XCLEventGroup.SYNC_UI_EVENT),
	getTextInput(XCLEventGroup.SYNC_UI_EVENT),
	setTextInput(XCLEventGroup.ASYNC_UI_EVENT);
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
