package van.xcl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.util.sf.StringFilter;
import van.xcl.XCLVar;

public class XCopy implements Command {
	
	@Override
	public String name() {
		return "xcp";
	}
	
	@Override
	public String description() {
		return "copy files with filter";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		ParameterValidator pathValidator = new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String path = value.toString();
				if (!new File(path).exists()) {
					throw new ParameterException("Cannot find the specified path: " + path);
				}
			}
		};
		ParameterValidator filterValidator = new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String filter = value.toString();
				StringFilter fileFilter = new StringFilter(filter);
				fileFilter.accept(""); // test
			}
		};
		parameters.add("src_path", pathValidator);
		parameters.add("dest_path");
		parameters.add("filter", filterValidator);
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		try {
			String src = args.get("src_path").toString();
			String des = args.get("dest_path").toString();
			String filter = args.get("filter").toString();
			StringFilter sf = new StringFilter(filter);
			AtomicInteger count = new AtomicInteger(0);
			File srcFile = new File(src);
			File desFile = new File(des);
			console.prompt("Files are being copied... [" + srcFile.getName() + "]");
			copy(count, srcFile, desFile, console, sf);
			console.prompt("Total files copied: " + count.get());
			return new XCLVar("  --> " + src + " " + des + " " + filter);
		} catch (IOException e) {
			console.error("IOException: " + e.getMessage());
		}
		return null;
	}
	
	private void copy(AtomicInteger count, File src, File des, XCLConsole console, StringFilter sf) throws IOException {
		if (src.exists()) {
			if (src.isDirectory()) {
				if (!des.exists()) {
					des.mkdirs();
				}
				for (File f : src.listFiles()) {
					String filename = f.getName();
					File d = new File(des, filename);
					copy(count, f, d, console, sf);
				}
			} else {
				if (sf == null || sf.accept(src.getName())) {
					if (!des.exists()) {
						des.createNewFile();
					}
					FileInputStream is = new FileInputStream(src);
					FileOutputStream os = new FileOutputStream(des);
					copy(is, os);
					console.prompt("Files are being copied... [" + count.get() + "]");
					// console.output("Copied: " + src.getAbsolutePath() + " --> " + des.getAbsolutePath()); // May cause AWT NullPointerException
					count.incrementAndGet();
				}
			}
		}
	}
	
	public void copy(InputStream fis, OutputStream fos) throws IOException {
		byte[] buff = new byte[1024];
		int length = -1;
		while (-1 != (length = fis.read(buff))) {
			fos.write(buff, 0, length);
		}
		fos.flush();
	}

}
