package van.xcl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import van.util.CommonUtils;
import van.util.evt.EventEntity;
import van.util.evt.EventManager;
import van.util.io.IoSerilizer;
import van.util.task.Task;
import van.util.task.TaskService;

public class XCLEventSyncer {
	
	class XCLEventReceiver implements Task {
		private Socket s;
		public XCLEventReceiver(Socket s) {
			this.s = s;
		}
		public void run() {
			try {
				System.out.println("XCLEventReceiver is running...");
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = null;
				while (null != (line = br.readLine())) {
					IoSerilizer<EventEntity> s = new IoSerilizer<EventEntity>(line);
					eventManager.addEvent(s.getObject());
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (connectSender != null) {
					connectSender.close();
					connectSender = null;
					console.info("XCLEventReceiver - Remote connection is disconnected.");
				}
				if (acceptSender != null) {
					acceptSender.close();
					acceptSender = null;
					console.info("XCLEventReceiver - Local connection is disconnected.");
				}
			} finally {
				console.info("Connection is disconnected.");
				console.prepare();
				console.editable(true);
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
				IoSerilizer<EventEntity> s = new IoSerilizer<EventEntity>(event);
				String xstring = s.getString();
				this.bw.write(xstring + "\r\n");
				this.bw.flush();
			} catch (IOException e) {
				if (connectSender != null) {
					connectSender.close();
					connectSender = null;
					console.info("XCLEventSender - Remote connection is disconnected.");
				}
				if (acceptSender != null) {
					acceptSender.close();
					acceptSender = null;
					console.info("XCLEventSender - Local connection is disconnected.");
				}
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
					System.out.println("0--------------> " + socket.toString());
					if (acceptSender == null) {
						connected(socket);
						acceptSender = new XCLEventSender(socket);
					} else {
						console.info("Client connection is skipped: " + socket.getRemoteSocketAddress().toString());
					}
				} catch (IOException e) {
					console.error(e.getMessage());
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
	private XCLEventSender connectSender;
	private XCLEventSender acceptSender;
	private XCLServerTask serverTask;
	private String source;
	private int port;
	
	public XCLEventSyncer(XCLConsole console, EventManager eventManager) {
		this.source = CommonUtils.getLocalIPAddress() + "@" + System.currentTimeMillis();
		this.console = console;
		this.eventManager = eventManager;
	}
	
	public void connect(String ip, int port) {
		if (connectSender == null) {
			try {
				Socket socket = new Socket(ip, port);
				connected(socket);
				connectSender = new XCLEventSender(socket);
				this.console.info("Server is connected: " + ip + ":" + port);
			} catch (UnknownHostException e) {
				console.error("Server connect failed: " + e.getMessage());
			} catch (IOException e) {
				console.error("Server connect failed: " + e.getMessage());
			} finally {
				console.prepare();
				console.editable(true);
			}
		} else {
			console.error("it is already connected!");
		}
	}
	
	public void disconnect() {
		if (connectSender != null) {
			connectSender.close();
		} else if (acceptSender != null) {
			acceptSender.close();
		}
	}
	
	public void syncEvent(EventEntity event) {
		System.out.println("XCLEventReceiver.syncEvent: isAccepted=" + isAccepted() + ",isConnected=" + isConnected());
		if (connectSender != null) {
			connectSender.send(event);
		} else if (acceptSender != null) {
			acceptSender.send(event);
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
				this.console.info("[" + source + "] Server startup succeed: " + port);
				isSuccess = true;
			} catch (IOException e) {
				this.console.error("[" + source + "] Server startup failed: " + port + " error: " + e.getMessage());
				port++;
				offset++;
			}
		} while (!isSuccess && offset < XCLConstants.DEFAULT_PORT_OFFSET);
		if (isSuccess) {
			this.isRunning.set(true);
			this.serverTask = new XCLServerTask(serverSocket);
			TaskService.getService().runTask(serverTask);
		} else {
			JOptionPane.showMessageDialog(null, "[" + source + "] startup failed!");
		}
	}
	
	public boolean isConnected() {
		return connectSender != null;
	}
	
	public boolean isAccepted() {
		return acceptSender != null;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public int getPort() {
		return this.port;
	}
	
	// ------------ private methods 
	
	private void connected(Socket socket) throws IOException {
		XCLEventReceiver receiver = new XCLEventReceiver(socket);
		TaskService.getService().runTask(receiver);
	}
	
}
