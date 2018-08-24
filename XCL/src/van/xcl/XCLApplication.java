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

import org.apache.log4j.Logger;

import van.util.CommonUtils;
import van.util.evt.EventCallback;
import van.util.evt.EventEntity;
import van.util.evt.EventHandler;
import van.util.evt.EventManager;
import van.util.task.TaskService;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLHealthChecker.XCLHealthEntity;

public class XCLApplication implements XCLConsole, XCLHandler, EventHandler, XCLHealthEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1247965919743709171L;
	
	private Logger logger = Logger.getLogger(getClass());

	private XCLUI ui = null;
	private XCLContext context = null;
	private XCLCmdParser parser = null;
	private XCLCmdHolder holder = null;
	private String contextFile;
	private EventManager eventManager = null;
	private XCLEventSyncer eventSyncer = null;
	private List<String> cmdLineList = new ArrayList<String>();
	private int currIndex = 0;
	private String lockFile;
	
	
	public XCLApplication() {
	}
	
	@Override
	public void startup(XCLStartupParas paras) {
		TaskService.getService().init("XCL", 10);
		this.ui = new XCLUI(this);
		this.eventManager = new EventManager();
		this.eventManager.register(XCLEventGroup.SYNC_UI_EVENT, ui);
		this.eventManager.register(XCLEventGroup.ASYNC_UI_EVENT, ui);
		this.eventManager.register(XCLEventGroup.CMD_EVENT, this);
		this.eventSyncer = new XCLEventSyncer(this, eventManager);
		int port = this.eventSyncer.startup();
		createLockFile(port);
		this.parser = new XCLCmdParser();
		this.holder = new XCLCmdHolder();
		this.contextFile = getContextFile(paras.getPara("context"));
		loadContext(this.contextFile);
		loadCommands();
		this.ui.init();
		XCLHealthChecker.getChecker().register(this);
		invokeStartup(paras.getPara("startup"));
	}
	
	@Override
	public void shutdown() {
		this.eventSyncer.shutdown();
		XCLHealthChecker.getChecker().shutdown();
		saveContext(this.contextFile);
		this.holder.clear();
		this.eventManager.shutdown();
		this.ui.dispose();
		TaskService.getService().shutdown();
		removeLockFile();
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
			} else if (e instanceof ThreadDeath) {
				throw new ThreadDeath();
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
		List<String> list = eventManager.stopAll(XCLEventGroup.CMD_EVENT);
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
	public boolean prepareEvent(EventEntity e) {
		if (this.getSource().equals(e.getSource())) {
			if (this.eventSyncer.hasConnectors()) {
				this.syncEvent(e); // synchronize local command event to remote
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String handleEvent(EventEntity event) {
		if (XCLEvent.run.equals(event.getType())) {
			execute(event.getMessage(), context);
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
			if (e instanceof ThreadDeath) {
				throw new ThreadDeath();
			} else {
				info(CommonUtils.getStackTrace(e));
				error(e.getMessage());
			}
		} finally {
			prepare();
			editable(true);
		}
		return result;
	}
	
	@Override
	public void run(String command) {
		this.eventManager.addEvent(this, XCLEvent.run, command);
	}

	@Override
	public void present(String string) {
		this.eventManager.addEvent(this, XCLEvent.present, string);
	}

	@Override
	public void prepare() {
		this.eventManager.addEvent(this, XCLEvent.prepare, null);
	}
	
	@Override
	public void prompt(String prompt) {
		prompt = "  -  " + CommonUtils.resolveString(prompt, 90);
		this.eventManager.addEvent(this, XCLEvent.prompt, prompt);
	}

	@Override
	public void input(String input) {
		this.eventManager.addEvent(this, XCLEvent.input, input);
	}

	@Override
	public void output(String str) {
		this.output(str, -1);
	}

	@Override
	public void info(String info) {
		this.info(info, -1);
	}
	
	@Override
	public void error(String error) {
		this.error(error, -1);
	}
	
	@Override
	public void output(String str, int traceId) {
		this.eventManager.addEvent(this, XCLEvent.output, str, traceId);
	}

	@Override
	public void info(String info, int traceId) {
		this.eventManager.addEvent(this, XCLEvent.info, info, traceId);
	}
	
	@Override
	public void error(String error, int traceId) {
		this.eventManager.addEvent(this, XCLEvent.error, error, traceId);
	}

	@Override
	public void title(String title) {
		this.eventManager.addEvent(this, XCLEvent.title, title);
	}
	
	@Override
	public void clear() {
		this.eventManager.addEvent(this, XCLEvent.clear, null);
	}

	@Override
	public void editable(boolean b) {
		this.eventManager.addEvent(this, XCLEvent.editable, String.valueOf(b));
	}
	
	@Override
	public void fixedRow(boolean b, int traceId) {
		this.eventManager.addEvent(this, XCLEvent.fixedRow, String.valueOf(b), traceId);
	}
	
	@Override
	public void setTextInput(String text) {
		this.eventManager.addEvent(this, XCLEvent.setTextInput, text);
	}
	
	@Override
	public String getTextInput(String text, String title) {
		this.eventManager.addEvent(this, XCLEvent.textInput, text);
		this.eventManager.addEvent(this, XCLEvent.textTitle, title);
		EventCallback callback  = EventCallback.defaultCallback();
		this.eventManager.addEvent(this, XCLEvent.getTextInput, text, callback);
		return callback.awaitResult();
	}
	
	@Override
	public void addKey(String key) {
		this.ui.addKey(key);
	}

	@Override
	public void removeKey(String key) {
		this.ui.removeKey(key);
	}

	@Override
	public void exit(int status) {
		XCLStartup.shutdown(this, status);
		// System.exit(status);
	}
	
	@Override
	public Map<String, Command> commands() {
		return this.holder.allCommands();
	}
	
	@Override
	public String getSource() {
		return this.eventSyncer.getSource();
	}
	
	@Override
	public boolean hasConnectors() {
		return this.eventSyncer.hasConnectors();
	}

	@Override
	public boolean hasAcceptors() {
		return this.eventSyncer.hasAcceptors();
	}
	
	@Override
	public int getPort() {
		return this.eventSyncer.getPort();
	}
	
	@Override
	public void connect(String ip, int port) {
		this.eventSyncer.connect(ip, port);
	}

	@Override
	public void disconnect() {
		this.eventSyncer.disconnect();
	}
	
	@Override
	public void syncEvent(EventEntity e) {
		this.eventSyncer.syncEvent(e);
	}
	
	@Override
	public void onHealthCheck() {
		if (!getLockFile().exists()) {
			this.exit(-1);
		}
	}
	
	// ------------------ private methods
	

	private XCLApplication getInstance() {
		return this;
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
	
	
	private void invokeStartup(String startupFile) {
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
		prepare();
		editable(true);
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
		File settingFile = new File(XCLConstants.SETTING_FILE);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter((settingFile)));
			bw.write(this.contextFile);
			bw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
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
	
	private void loadCommands() {
		List<Class<? extends Command>> list = CommandLoader.loadCommandClasses();
		for (Class<? extends Command> clazz : list) {
			try {
				Command command = clazz.newInstance();
				this.holder.addCommand(command.name(), command);
				logger.info("--> " + command.name() + " is loaded.");
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createLockFile(int port) {
		this.lockFile = port + ".lock";
		boolean isSuccess = false;
		try {
			isSuccess = getLockFile().createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!isSuccess) {
				error("The lock file was not created properly, it may not be gracefully closed last time.");
			}
		}
	}
	
	private void removeLockFile() {
		getLockFile().delete();
	}
	
	private File getLockFile() {
		File file = new File(lockFile);
		return file;
	}
	
	private String delimeter() {
		StringBuilder delimeter = new StringBuilder();
		for (int i = 0 ; i < 72 ; i++) {
			delimeter.append("-");
		}
		return delimeter.toString();
	}

}
