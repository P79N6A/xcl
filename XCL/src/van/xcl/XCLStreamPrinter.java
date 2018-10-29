package van.xcl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import van.util.task.Task;
import van.util.task.TaskService;

public class XCLStreamPrinter {
	public enum Type {
		INF,
		ERR,
		OUT
	}
	private InputStream is;
	private String prefix;
	public XCLStreamPrinter(InputStream is, String prefix) {
		this.is = is;
		this.prefix = prefix;
	}
	
	public void print(XCLConsole console, Type type) {
		TaskService.getService().runTask(new Task() {
			public void run() {
				print0(console, type);
			}
		});
	}
	
	private void print0(XCLConsole console, Type type) {
		String line = null;
		try {
			InputStreamReader isr = new InputStreamReader(is, "GBK");
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				String message = "[" + prefix + "] " + line;
				if (Type.INF.equals(type)) {
					console.info(message);
				} else if (Type.ERR.equals(type)) {
					console.error(message);
				} else if (Type.OUT.equals(type)) {
					console.output(message);
				}
			}
			br.close();
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
