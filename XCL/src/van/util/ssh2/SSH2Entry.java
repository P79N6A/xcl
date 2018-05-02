package van.util.ssh2;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

public class SSH2Entry {
	
	private String hostname;
	private String username;
	private String password;
	
	private Connection connection;
	private SCPClient client;
	private boolean isConnected = false;
	
	public SSH2Entry(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}
	
	public void connect() throws IOException {
		this.connection = new Connection(hostname);
		this.connection.connect();
		if (!this.connection.authenticateWithPassword(username, password)) {
			throw new IOException("Authentication failed.");
		}
		this.client = connection.createSCPClient();
		this.isConnected = true;
	}
	
	public Session openSession() throws IOException {
		return this.connection.openSession();
	}
	
	public void closeSession(Session session) {
		if (session != null){
			session.close();
		}
	}
	
	public SCPClient getClient() {
		return this.client;
	}
	
	public void close() {
		if (this.isConnected) {
			connection.close();
			this.isConnected = false;
		}
	}
	
	public String toString() {
		return hostname + "/" + username;
	}

}
