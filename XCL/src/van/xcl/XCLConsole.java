package van.xcl;

import java.util.Map;

import van.util.evt.EventEntity;
import van.util.evt.EventSource;

public interface XCLConsole extends EventSource {
	public String getTextInput(String text, String tile);
	public void prepare();
	public void editable(boolean b);
	public void input(String input);
	public void output(String output);
	public void info(String info);
	public void error(String error);
	public void run(String command);
	public XCLResult execute(String input, XCLContext context);
	public void title(String str);
	public void prompt(String str);
	public void present(String str);
	public void exit(int status);
	public void clear();
	public Map<String, Command> commands();
	public void cancelCommand();
	public void setHistoryIndex(int index);
	public int getHistoryIndex();
	public int getHistorySize();
	public String getHistory(int index);
	public void saveHistory(String cmd);
	public void connect(String ip, int port);
	public void disconnect();
	public boolean isConnected();
	public boolean isAccepted();
	public int getPort();
	public void syncEvent(EventEntity e);
}
