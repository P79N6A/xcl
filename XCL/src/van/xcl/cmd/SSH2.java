package van.xcl.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import van.util.CommonUtils;
import van.util.ZipUtils;
import van.util.ssh2.SSH2Client;
import van.xcl.Command;
import van.xcl.CommandException;
import van.xcl.ParameterException;
import van.xcl.ParameterValidator;
import van.xcl.Parameters;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLConsole;
import van.xcl.XCLContext;
import van.xcl.XCLParameters;
import van.xcl.XCLVar;

public class SSH2 implements Command {

	@Override
	public String name() {
		return "ssh2";
	}

	@Override
	public String description() {
		return "ssh2";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("parameter", new ParameterValidator() {
			@Override
			public void validate(XCLContext context, XCLVar value) throws ParameterException {
				XCLParameters.validate(value, "hostname", "username", "password", "action");
				String action = XCLParameters.resolveXCLParas(value.toString()).getValue("action");
				if (!"cmd".equals(action) && !"get".equals(action) && !"put".equals(action)) {
					throw new ParameterException("The allowable value of parameter [action]: exec/get/put");
				}
				if ("put".equals(action)) {
					XCLParameters.validate(value, "localFile", "remoteDir");
				}
				if ("get".equals(action)){
					XCLParameters.validate(value, "localDir", "remoteFile");
				}
			}
		});
		parameters.add("command");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) throws CommandException {
		XCLParameters map = XCLParameters.resolveXCLParas(args.get("parameter").toString());
		String hostname = map.getValue("hostname");
		String username = map.getValue("username");
		String password = map.getValue("password");
		String action = map.getValue("action");
		String command = args.get("command").toString();
		SSH2Client client = new SSH2Client(hostname, username, password);
		try {
			client.connect();
			if ("cmd".equals(action)) {
				exec(client, command, console);
			} else {
				if ("get".equals(action)) {
					String remoteFile = map.getValue("remoteFile");
					String localDir = map.getValue("localDir");
					String remoteFilePath = remoteFile.substring(0, remoteFile.lastIndexOf("/"));
					String remoteFileName = remoteFile.substring(remoteFile.lastIndexOf("/") + 1);
					String remoteFileZip = remoteFileName + ".zip";
					// compress file
					console.info("[" + client.toString() + "] compress remote file...");
					exec(client, "cd " + remoteFilePath + ";zip -r " + remoteFileZip + " ./" + remoteFileName, console);
					// download file
					console.info("[" + client.toString() + "] [" + remoteFilePath + " --> " + localDir + "] getting...");
					client.getClient().get(remoteFilePath + "/" + remoteFileZip, localDir);
					console.info("[" + client.toString() + "] [" + remoteFilePath + " --> " + localDir + "] get end");
					// remove compressed filed
					console.info("[" + client.toString() + "] remove remote zip file...");
					exec(client, "cd " + remoteFilePath + ";rm " + remoteFileZip, console);
					// uncompress file
					File localZipFile = new File(localDir, remoteFileZip);
					console.info("[" + client.toString() + "] uncompress local zip file...");
					ZipUtils.unzip(localZipFile.getAbsolutePath(), localDir, false);
					console.info("[" + client.toString() + "] delete local zip file...");
					localZipFile.delete();
					console.info("[" + client.toString() + "] done.");
				} else if ("put".equals(action)) {
					String localFile = map.getValue("localFile");
					String remoteDir = map.getValue("remoteDir");
					File file = new File(localFile);
					String zipFileName = file.getName() + ".zip";
					File localZipFile = new File(new File(localFile).getParent(), zipFileName);
					// compress file
					console.info("[" + client.toString() + "] compress local file...");
					ZipUtils.zip(localFile, localZipFile.getParent(), localZipFile.getName());
					// remove remote file if any
					console.info("[" + client.toString() + "] remove remote file if any...");
					exec(client, "cd " + remoteDir + ";rm " + file.getName(), console);
					// put file
					console.info("[" + client.toString() + "] [" + localZipFile.getAbsolutePath() + " --> " + remoteDir + "] putting...");
					client.getClient().put(localZipFile.getAbsolutePath(), remoteDir);
					console.info("[" + client.toString() + "] [" + localZipFile.getAbsolutePath() + " --> " + remoteDir + "] put end");
					// unzip file
					console.info("[" + client.toString() + "] uncompress remote zip file...");
					exec(client, "cd " + remoteDir + ";unzip " + zipFileName, console);
					// remove remote zip file
					console.info("[" + client.toString() + "] remove remote zip file...");
					exec(client, "cd " + remoteDir + ";rm " + zipFileName, console);
					// remove local zip file
					console.info("[" + client.toString() + "] remove local zip file...");
					localZipFile.delete();
					console.info("[" + client.toString() + "] done.");
				}
			}
		} catch (IOException e) {
			console.error(CommonUtils.getStackTrace(e));
		} finally {
			client.close();
		}
		return new XCLVar();
	}
	
	private void exec(SSH2Client client, String command, XCLConsole console) throws IOException {
		Session session = client.openSession();
		try {
			if (command.startsWith("\"") && command.endsWith("\"")) {
				command = command.substring(1, command.length() - 1);
			}
			console.info("[" + client.toString() + "] command: " + command);
			session.execCommand(command);
			InputStream stdout = new StreamGobbler(session.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
			String line = null;
			while (null != (line = br.readLine())) {
				console.output("[" + client.toString() + "] - " + line);
			}
			br.close();
			session.waitForCondition(ChannelCondition.EXIT_STATUS, 30000L);
			console.info("[" + client.toString() + "] command done. [Exit Status: " + session.getExitStatus() + "]");
		} finally {
			client.closeSession(session);
		}
	}
	
}
