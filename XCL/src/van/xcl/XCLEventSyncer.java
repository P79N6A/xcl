package van.xcl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import van.util.CommonUtils;
import van.util.ObjectSerilizer;
import van.util.evt.EventEntity;
import van.util.evt.EventManager;
import van.util.task.Task;
import van.util.task.TaskService;

public class XCLEventSyncer {
	
	private Logger logger = Logger.getLogger(getClass());
	
	class XCLEventReceiver implements Task {
		private Socket s;
		public XCLEventReceiver(Socket s) {
			this.s = s;
		}
		public void run() {
			try {
				logger.info("XCLEventReceiver is running...");
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = null;
				while (null != (line = br.readLine())) {
					ObjectSerilizer<EventEntity> s = new ObjectSerilizer<EventEntity>(line);
					eventManager.addEvent(s.getObject());
				}
			} catch (IOException e) {
				onSocketClosed(s);
				console.info("XCLEventReceiver - Remote connection is disconnected.");
			} finally {
				console.info("Connection is disconnected.");
				console.prepare();
				console.editable(true);
			}
		}
		public void close() {
			if (this.s != null) {
				try {
					this.s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class XCLEventSender {
		private BufferedWriter bw = null;
		private Socket s = null;
		public XCLEventSender(Socket s) throws IOException {
			this.s = s;
			this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		}
		public void send(EventEntity event) {
			try {
				ObjectSerilizer<EventEntity> s = new ObjectSerilizer<EventEntity>(event);
				String xstring = s.getString();
				this.bw.write(xstring + "\r\n");
				this.bw.flush();
			} catch (IOException e) {
				onSocketClosed(s);
				console.info("XCLEventReceiver - Remote connection is disconnected.");
				e.printStackTrace();
			}
		}
		public void close() {
			try {
				this.s.close();
				this.bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public Socket getSocket() {
			return s;
		}
		@Override
		public boolean equals(Object other) {
			if (other instanceof XCLEventSender) {
				return this.s.equals(((XCLEventSender) other).s);
			}
			return false;
		}
	}

	class XCLServerTask implements Task {
		private ServerSocket ss;
		public XCLServerTask(ServerSocket ss) {
			this.ss = ss;
		}
		public void run() {
			while (isRunning.get()) {
				try {
					Socket socket = ss.accept();
					XCLEventReceiver receiver = new XCLEventReceiver(socket);
					TaskService.getService().runTask(receiver);
					XCLEventSender acceptor = new XCLEventSender(socket);
					acceptors.add(acceptor);
				} catch (IOException e) {
					if (isRunning.get()) {
						console.error(e.getMessage());
					}
				}
			}
		}
		public void close() {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private XCLConsole console;
	private EventManager eventManager;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private Vector<XCLEventSender> connectors = new Vector<XCLEventSender>();
	private Vector<XCLEventSender> acceptors = new Vector<XCLEventSender>();
	
	private XCLServerTask serverTask;
	private String source;
	private int port;
	
	public XCLEventSyncer(XCLConsole console, EventManager eventManager) {
		this.source = CommonUtils.getLocalIPAddress() + "@" + System.currentTimeMillis();
		this.console = console;
		this.eventManager = eventManager;
	}
	
	public void connect(String ip, int port) {
		try {
			Socket socket = new Socket(ip, port);
			XCLEventReceiver receiver = new XCLEventReceiver(socket);
			TaskService.getService().runTask(receiver);
			XCLEventSender connector = new XCLEventSender(socket);
			connectors.add(connector);
			this.console.info("Server is connected: " + ip + ":" + port);
		} catch (UnknownHostException e) {
			console.error("Server connect failed: " + e.getMessage());
		} catch (IOException e) {
			console.error("Server connect failed: " + e.getMessage());
		} finally {
			console.prepare();
			console.editable(true);
		}
	}
	
	public void disconnect() {
		synchronized (connectors) {
			for (XCLEventSender sender : connectors) {
				sender.close();
			}
		}
		synchronized (acceptors) {
			for (XCLEventSender sender : acceptors) {
				sender.close();
			}
		}
	}
	
	public void syncEvent(EventEntity event) {
		logger.info("XCLEventReceiver.syncEvent: hasAcceptors=" + hasAcceptors() + ",hasConnectors=" + hasConnectors());
		synchronized (connectors) {
			for (XCLEventSender sender : connectors) {
				sender.send(event);
			}
		}
		synchronized (acceptors) {
			for (XCLEventSender sender : acceptors) {
				sender.send(event);
			}
		}
	}
	
	public void shutdown() {
		disconnect();
		this.isRunning.compareAndSet(true, false);
		if (this.serverTask != null) {
			this.serverTask.close();
		}
	}
	
	public void startup() {
		int port = XCLConstants.DEFAULT_PORT;
		int offset = 0;
		boolean isSuccess = false;
		ServerSocket serverSocket = null;
		do {
			try {
				serverSocket = new ServerSocket(port);
				this.port = port;
				isSuccess = true;
			} catch (IOException e) {
				port++;
				offset++;
			}
		} while (!isSuccess && offset < XCLConstants.DEFAULT_PORT_OFFSET);
		if (isSuccess) {
			this.source = CommonUtils.getLocalIPAddress() + ":" + this.port;
			this.isRunning.set(true);
			this.serverTask = new XCLServerTask(serverSocket);
			TaskService.getService().runTask(serverTask);
			this.console.info("[" + source + "] Server startup succeed");
		} else {
			JOptionPane.showMessageDialog(null, "[" + source + "] startup failed!");
		}
	}
	
	public boolean hasConnectors() {
		return connectors.size() > 0;
	}
	
	public boolean hasAcceptors() {
		return acceptors.size() > 0;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public int getPort() {
		return this.port;
	}
	
	// ------------ private methods 
	
	private void onSocketClosed(Socket s) {
		synchronized (connectors) {
			for (XCLEventSender sender : connectors) {
				if (sender.getSocket().equals(s)) {
					sender.close();
					connectors.remove(sender);
				}
			}
		}
		synchronized (acceptors) {
			for (XCLEventSender sender : acceptors) {
				if (sender.getSocket().equals(s)) {
					sender.close();
					acceptors.remove(sender);
				}
			}
		}
	}
	
}
