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
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCommandNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLVar;

public class FolderDiff implements Command {
	
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
	
	private void writeResult(XCLConsole console, File outputFile, List<String> diffList, List<String> deleteList, List<String> addList, String path1, String path2) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String header = " **** Compare Results [" + path1 + " vs " + path2 + "] **** ";
		bw.append(header);
		console.output(header);
		bw.newLine();
		String modified = " - Modified : " + diffList.size();
		bw.append(modified);
		console.output(modified);
		bw.newLine();
		String deleted = " - Deleted  : " + deleteList.size();
		bw.append(deleted);
		console.output(deleted);
		bw.newLine();
		String added = " - Added    : " + addList.size();
		bw.append(" - Added    : " + addList.size());
		console.output(added);
		bw.newLine();
		bw.flush();
		for (String path : diffList) {
			// Modified
			bw.append("[*] " + path);
			console.output("[*] " + path);
			bw.newLine();
			bw.flush();
		}
		for (String path : deleteList) {
			// Deleted
			bw.append("[-] " + path);
			console.output("[-] " + path);
			bw.newLine();
			bw.flush();
		}
		for (String path : addList) {
			// Added
			bw.append("[+] " + path);
			console.output("[+] " + path);
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}
	
	private void diff0(XCLConsole console, boolean compareDiff, File file, File dir1, File dir2, List<String> diffList, List<String> missList) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				console.prompt("processing : " + file.getAbsolutePath());
				for (File f : file.listFiles()) {
					diff0(console, compareDiff, f, dir1, dir2, diffList, missList);
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
	
	public void diff(File prevFile, File currFile, File outputFile, XCLConsole console) throws IOException {
		// compare old to new to found diff & delete files
		List<String> diffList = new ArrayList<String>(); // different files: modified files
		List<String> deleteList = new ArrayList<String>(); // missing files in currFile: deleted files
		diff0(console, true, prevFile, prevFile, currFile, diffList, deleteList);
		// compare new to old to found add files
		List<String> addList = new ArrayList<String>(); // missing files in prevFile: added files
		diff0(console, false, currFile, currFile, prevFile, null, addList);
		writeResult(console, outputFile, diffList, deleteList, addList, prevFile.getAbsolutePath(), currFile.getAbsolutePath());
	}
	
	@Override
	public String name() {
		return "folderdiff";
	}

	@Override
	public String description() {
		return "folder difference";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		ParameterValidator folderValidator = new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				File file = new File(value.toString());
				if (!file.exists()) {
					throw new ParameterException("[" + file + "] File does not exist!");
				}
				if (!file.isDirectory()) {
					throw new ParameterException("[" + file + "] File is not a directory!");
				}
			}
		};
		parameters.add("previous_folder", folderValidator);
		parameters.add("current_folder", folderValidator);
		parameters.add("result_file");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLCommandNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		String prev = args.get("previous_folder").toString();
		String curr = args.get("current_folder").toString();
		String file = args.get("result_file").toString();
		try {
			diff(new File(prev), // previous version folder
					new File(curr), // new version folder
					new File(file), // compare results file
					console);
		} catch (IOException e) {
			throw new CommandException(e.getMessage());
		}
		return new XCLVar(file);
	}

}
