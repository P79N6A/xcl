package van.xcl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import van.util.CommonUtils;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCommandNode;
import van.xcl.XCLVar;
import van.xcl.util.sf.StringFilter;

public class Copy implements Command {

	@Override
	public String name() {
		return "cp";
	}

	@Override
	public String description() {
		return "copy files";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("src_path", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				String path = value.toString();
				if (!new File(path).exists()) {
					throw new ParameterException("Cannot find the specified path: " + path);
				}
			}
		});
		parameters.add("dest_path");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		try {
			String src = args.get("src_path").toString();
			String des = args.get("dest_path").toString();
			AtomicInteger count = new AtomicInteger(0);
			File srcFile = new File(src);
			File desFile = new File(des);
			console.prompt("Files are being copied... [" + srcFile.getName() + "]");
			copy(context, count, srcFile, desFile, console, null);
			console.prompt("Total files copied: " + count.get());
		} catch (IOException e) {
			console.error(CommonUtils.getStackTrace(e));
		}
		return new XCLVar();
	}

	private void copy(XCLContext context, AtomicInteger count, File src, File des, XCLConsole console, StringFilter sf) throws IOException {
		if (src.exists()) {
			if (src.isDirectory()) {
				if (!des.exists()) {
					des.mkdirs();
				}
				for (File f : src.listFiles()) {
					String filename = f.getName();
					File d = new File(des, filename);
					copy(context, count, f, d, console, sf);
				}
			} else {
				if (sf == null || sf.accept(src.getName())) {
					createNewFile(des);
					FileInputStream is = new FileInputStream(src);
					FileOutputStream os = new FileOutputStream(des);
					copy(is, os);
					os.close();
					is.close();
					console.prompt("Files are being copied... [" + count.get() + "]");
					count.incrementAndGet();
				}
			}
		}
	}

	private void copy(InputStream fis, OutputStream fos) throws IOException {
		byte[] buff = new byte[8192];
		int length = 0;
		while (0 < (length = fis.read(buff))) {
			fos.write(buff, 0, length);
		}
		fos.flush();
	}

	private void createNewFile(File file) throws IOException {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			file.createNewFile();
		}
	}

}
