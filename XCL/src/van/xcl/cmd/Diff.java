package van.xcl.cmd;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class Diff implements Command {
	
	@Override
	public String name() {
		return "diff";
	}
	
	@Override
	public String description() {
		return "find folder differences";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("previousFolder");
		parameters.add("currentFolder");
		parameters.add("resultFile");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String previousFolder = args.get("previousFolder").toString();
		String currentFolder = args.get("currentFolder").toString();
		String resultFile = args.get("resultFile").toString();
		int traceId = this.hashCode();
		console.fixedRow(true, traceId);
		try {
			diff(new File(previousFolder), new File(currentFolder), new File(resultFile), console, traceId);
		} catch (IOException e) {
			throw new CommandException(e.getMessage());
		} finally {
			console.fixedRow(false, traceId);
		}
		return new XCLVar(resultFile);
	}
	
	private String genFileMd5(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			try {
				MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(byteBuffer);
				BigInteger bi = new BigInteger(1, md5.digest());
				return bi.toString(16);
			} finally {
				if (null != in) {
					in.close();
				}
			}
		} catch (Throwable e) {
			// Unexpected.
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasDiff(File file1, File file2) {
		String m1 = genFileMd5(file1);
		String m2 = genFileMd5(file2);
		return !m1.equals(m2);
	}
	
	private void writeResult(File outputFile, List<String> diffList, List<String> deleteList, List<String> addList, String path1, String path2, XCLConsole console, int traceId) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.append(" **** Compare Results [" + path1 + " vs " + path2 + "] **** ");
		bw.newLine();
		bw.append(" - Modified : " + diffList.size());
		bw.newLine();
		bw.append(" - Deleted  : " + deleteList.size());
		bw.newLine();
		bw.append(" - Added    : " + addList.size());
		bw.newLine();
		bw.flush();
		for (String path : diffList) {
			// Modified
			bw.append("[*] " + path);
			bw.newLine();
			bw.flush();
		}
		for (String path : deleteList) {
			// Deleted
			bw.append("[-] " + path);
			bw.newLine();
			bw.flush();
		}
		for (String path : addList) {
			// Added
			bw.append("[+] " + path);
			bw.newLine();
			bw.flush();
		}
		bw.close();
		console.output(" **** Compare Results [" + path1 + " vs " + path2 + "] **** \r\n - Modified : " + diffList.size() + "\r\n - Deleted  : " + deleteList.size() + "\r\n - Added    : " + addList.size(), traceId);
	}
	
	private void diff0(boolean compareDiff, File file, File dir1, File dir2, List<String> diffList, List<String> missList, XCLConsole console, int traceId) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				console.info(" - process [" + file.getAbsolutePath() + "]", traceId);
				for (File f : file.listFiles()) {
					diff0(compareDiff, f, dir1, dir2, diffList, missList, console, traceId);
				}
			} else {
				File file1 = file;
				File file2 = new File(file.getAbsolutePath().replace(dir1.getAbsolutePath(), dir2.getAbsolutePath()));
				String path = file.getAbsolutePath().replace(dir1.getAbsolutePath(), "");
				if (!file2.exists()) {
					missList.add(path);
				} else if (compareDiff && hasDiff(file1, file2)) {
					diffList.add(path);
				}
			}
		}
	}
	
	public void diff(File prevFile, File currFile, File outputFile, XCLConsole console, int traceId) throws IOException {
		if (!prevFile.isDirectory() || !currFile.isDirectory()) {
			throw new IllegalArgumentException("Directory is allowed here!");
		}
		// compare old to new to found diff & delete files
		List<String> diffList = new ArrayList<String>(); // different files: modified files
		List<String> deleteList = new ArrayList<String>(); // missing files in currFile: deleted files
		diff0(true, prevFile, prevFile, currFile, diffList, deleteList, console, traceId);
		// compare new to old to found add files
		List<String> addList = new ArrayList<String>(); // missing files in prevFile: added files
		diff0(false, currFile, currFile, prevFile, null, addList, console, traceId);
		writeResult(outputFile, diffList, deleteList, addList, prevFile.getAbsolutePath(), currFile.getAbsolutePath(), console, traceId);
	}

}
