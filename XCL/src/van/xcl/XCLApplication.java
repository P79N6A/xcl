package van.xcl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import van.util.CommonUtils;
import van.util.evt.EventCallback;
import van.util.evt.EventHandler;
import van.util.evt.EventManager;
import van.util.evt.EventType;
import van.xcl.XCLCmdParser.XCLNode;

public class XCLApplication extends EventHandler implements XCLConsole, XCLHandler {
	
	private XCLUI ui = null;
	private XCLContext context = null;
	private XCLCmdParser parser = null;
	private XCLCmdHolder holder = null;
	private String contextFile;
	private EventManager eventManager = null;
	private List<String> cmdLineList = new ArrayList<String>();
	private int currIndex = 0;
	
	public XCLApplication() {
		
	}
	
	@Override
	public void launch(XCLStartupParas paras) {
		init(paras.getContext());
		loadContext(this.contextFile);
		registerCommand();
		this.ui.init();
		prepare();
		editable(true);
		startup(paras.getStartup());
	}
	
	@Override
	public void register(Class<? extends Command> clazz) {
		try {
			Command command = clazz.newInstance();
			this.holder.addCommand(command.name(), command);
			System.out.println("--> " + command.name() + " is loaded.");
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isCommand(String key) {
		return this.holder.isCommand(key);
	}
	
	@Override
	public boolean isScript(String key) {
		return this.context.containsCraft(key);
	}
	
	@Override
	public void shutdown() {
		saveContext(this.contextFile);
		File settingFile = new File(XCLConstants.SETTING_FILE);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter((settingFile)));
			bw.write(this.contextFile);
			bw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		this.holder.clear();
		this.eventManager.shutdown();
		this.ui.dispose();
	}
	
	@Override
	public XCLVar command(List<String> command, XCLConsole console, XCLContext context) {
		try {
			XCLNode node = this.parser.parseCommand(command, context, this.holder);
			if (node != null) {
				if (node.isExecutable()) {
					try {
						if (node.hasChilds()) {
							console.info(CommonUtils.getCurrentTimeString() + "\n " + delimeter() + "\n " + node.getFormatString().trim() + "\n " + delimeter());
						}
						return node.execute(console, context, this.holder, this);
					} catch (ParameterException e) {
						console.error(e.getMessage());
					}
				} else {
					console.error("\"" + node.getName() + "\" command not found.");
				}
			} else {
				console.error("unknown command");
			}
		} catch (Throwable e) {
			if (e instanceof CommandException) {
				console.error(e.getMessage());
			} else if (e instanceof ParameterException) {
				console.error(e.getMessage());
			} else {
				console.error(CommonUtils.getStackTrace(e));
			}
		}
		return null;
	}
	
	@Override
	public boolean switchContext(String context) {
		String currentFile = context.toUpperCase() + ".ctx";
		String originalFile = this.contextFile;
		if (!originalFile.equals(currentFile)) {
			File file = new File(currentFile);
			if (file.exists()) {
				this.contextFile = currentFile;
				saveContext(originalFile);
				loadContext(this.contextFile);
				return true;
			} else {
				error("Context file not found: " + currentFile);
			}
		} else {
			error("Current context is already '" + context + "'");
		}
		return false;
	}
	
	@Override
	public String currentContext() {
		return this.contextFile.replace(".ctx", "");
	}

	@Override
	public XCLContext getContext() {
		return this.context;
	}
	
	@Override
	public void cancelCommand() {
		info("Cancel command requested");
		List<String> list = eventManager.stopAll(XCLEventGroup.cmd);
		for (String info : list) {
			info(info + " thread is stopped.");
		}
	}

	@Override
	public void setHistoryIndex(int index) {
		this.currIndex = index;
	}
	@Override
	public int getHistoryIndex() {
		return this.currIndex;
	}
	@Override
	public int getHistorySize() {
		return this.cmdLineList.size();
	}
	@Override
	public String getHistory(int index) {
		return this.cmdLineList.get(index);
	}
	@Override
	public void saveHistory(String cmd) {
		if (cmdLineList.contains(cmd)) {
			cmdLineList.remove(cmd);
		}
		cmdLineList.add(cmd);
		currIndex = cmdLineList.size();
	}
	
	// ---------------------------
	
	@Override
	public String handle(EventType type, String message) {
		if (XCLEvent.run.equals(type)) {
			execute(message, context);
		}
		return null;
	}
	
	@Override
	public XCLResult execute(String input, XCLContext context) {
		XCLResult result = new XCLResult();
		result.setSuccess(false);
		try {
			editable(false);
			List<List<String>> commandList = XCLUtils.resolveCommands(input);
			for (int i = 0 ; i < commandList.size() ; i++) {
				List<String> commands = commandList.get(i);
				if (commands.size() > 0) {
					XCLVar var = command(commands, getInstance(), context);
					if (var == null) 
						break;
					result.addResult(var);
				}
			}
			if (commandList.size() == 1) {
				saveHistory(input);
			}
			int resultSize = result.getResults().size();
			if (resultSize == commandList.size()) {
				if (resultSize > 0) {
					result.setSuccess(true);
				}
			} else {
				if (resultSize > 0 && resultSize < commandList.size()) {
					error("Command execution is interrupted.");
				}
			}
		} catch (Throwable e) {
			info(CommonUtils.getStackTrace(e));
			error(e.getMessage());
		} finally {
			prepare();
			editable(true);
		}
		return result;
	}
	
	@Override
	public void run(String command) {
		this.eventManager.addEvent(XCLEvent.run, command);
	}

	@Override
	public void present(String string) {
		this.eventManager.addEvent(XCLEvent.present, string);
	}

	@Override
	public void prepare() {
		this.eventManager.addEvent(XCLEvent.prepare, null);
	}

	@Override
	public void input(String input) {
		this.eventManager.addEvent(XCLEvent.input, input);
	}

	@Override
	public void output(String str) {
		this.eventManager.addEvent(XCLEvent.output, str);
	}

	@Override
	public void prompt(String prompt) {
		prompt = "  -  " + CommonUtils.resolveString(prompt, 90);
		this.eventManager.addEvent(XCLEvent.prompt, prompt);
	}
	
	@Override
	public void info(String info) {
		this.eventManager.addEvent(XCLEvent.info, info);
	}
	
	@Override
	public void error(String error) {
		this.eventManager.addEvent(XCLEvent.error, error);
	}

	@Override
	public void title(String title) {
		this.eventManager.addEvent(XCLEvent.title, title);
	}
	
	@Override
	public void clear() {
		this.eventManager.addEvent(XCLEvent.clear, null);
	}

	@Override
	public void editable(boolean b) {
		this.eventManager.addEvent(XCLEvent.editable, String.valueOf(b));
	}
	
	@Override
	public String getTextInput(String text, String title) {
		this.eventManager.addEvent(XCLEvent.textInput, text);
		this.eventManager.addEvent(XCLEvent.textTitle, title);
		EventCallback callback  = EventCallback.defaultCallback();
		this.eventManager.addEvent(XCLEvent.getTextInput, text, callback);
		return callback.awaitResult();
	}
	
	@Override
	public void exit(int status) {
		shutdown();
		XCLStartup.shutdown(this, status);
		// System.exit(status);
	}
	
	@Override
	public Map<String, Command> commands() {
		return this.holder.allCommands();
	}
	
	// ------------------ private methods
	
	private XCLApplication getInstance() {
		return this;
	}
	
	private void init(String contextName) {
		this.ui = new XCLUI(this);
		this.eventManager = new EventManager();
		this.eventManager.register(XCLEventGroup.ui, ui);
		this.eventManager.register(XCLEventGroup.cmd, this);
		this.parser = new XCLCmdParser();
		this.holder = new XCLCmdHolder();
		this.contextFile = getContextFile(contextName);
	}
	
	private String getContextFile(String contextName) {
		if (!CommonUtils.isEmpty(contextName)) {
			return contextName.toUpperCase() + ".ctx";
		}
		File settingFile = new File(XCLConstants.SETTING_FILE);
		String contextFile = XCLConstants.DEFAULT_CONTEXT_FILE;
		try {
			if (settingFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader((settingFile)));
				String line = br.readLine();
				if (line != null) {
					contextFile = line;
				}
				br.close();
			}
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "[XCLApplication getContextFile error]: " + e.getMessage());
		}
		return contextFile;
	}
	
	
	private void startup(String startupFile) {
		if (!CommonUtils.isEmpty(startupFile)) {
			File file = new File(startupFile);
			if (file.exists()) {
				try {
					String startup = CommonUtils.readFileToString(file, "UTF-8");
					info("startup \r\n" + startup);
					run(startup);
				} catch (IOException e) {
					error(e.getMessage());
				}
			} else {
				error("Startup file not found: " + startupFile);
			}
		}
	}
	
	private void saveContext(String contextFile) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(contextFile));
			oos.writeObject(context);
		} catch (FileNotFoundException e) {
			error(e.getMessage());
		} catch (IOException e) {
			error(e.getMessage());
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadContext(String contextFile) {
		File file = new File(contextFile);
		if (file.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(file));
				this.context = (XCLContext) ois.readObject();
				this.ui.addKey(XCLConstants.PARAS_DEFAULT);
				for (String key : this.context.getCrafts().keySet()) {
					this.ui.addKey(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			this.context = new XCLContext();
			this.context.setPath(new File("").getAbsolutePath());
		}
		this.context.setHandler(this);
		this.title(context.getPath());
		this.info(contextFile);
	}
	
	private void registerCommand() {
		List<Class<? extends Command>> list = CommandLoader.loadCommandClasses();
		for (Class<? extends Command> clazz : list) {
			getInstance().register(clazz);
		}
	}
	
	private String delimeter() {
		StringBuilder delimeter = new StringBuilder();
		for (int i = 0 ; i < 72 ; i++) {
			delimeter.append("-");
		}
		return delimeter.toString();
	}

}
