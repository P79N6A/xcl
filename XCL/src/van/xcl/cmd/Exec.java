package van.xcl.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Exec implements Command {
	
	private class StreamHandler extends Thread {
		private InputStream is;
		private XCLConsole console;
		private String prefix;
		public StreamHandler(InputStream is, XCLConsole console, String prefix) {
			super("StreamHandler");
			this.is = is;
			this.console = console;
			this.prefix = prefix;
			this.setDaemon(true);
		}
		public void run() {
			String line = null;
			try {
				System.out.println("[StreamHandler-" + prefix + "] is started");
				InputStreamReader isr = new InputStreamReader(is, "GBK");
				BufferedReader br = new BufferedReader(isr);
				while ((line = br.readLine()) != null) {
					console.info("[" + prefix + "] " + line);
				}
				System.out.println("[StreamHandler-" + prefix + "] is ended");
			} catch (IOException e) {
				console.error("[" + prefix + "] " + "IOException: " + e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public String name() {
		return "exec";
	}
	
	@Override
	public String description() {
		return "execute commands";
	}
	
	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("script");
		return parameters;
	}

	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		String input = args.get("script").toString();
		try {
			console.prompt(input);
			Process proc = Runtime.getRuntime().exec(input, null, new File(context.getPath()));
			StreamHandler outHandler = new StreamHandler(proc.getInputStream(), console, "exec-out");
			StreamHandler errHandler = new StreamHandler(proc.getErrorStream(), console, "exec-err");
			outHandler.start();
			errHandler.start();
			int exitVal = proc.waitFor();
			console.info("[exec-out] exit value: " + exitVal);
		} catch (IOException e) {
			console.output("IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			console.output("InterruptedException: " + e.getMessage());
		}
		return new XCLVar();
	}
}
