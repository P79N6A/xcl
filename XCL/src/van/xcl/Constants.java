package van.xcl;

import java.awt.Font;

public class Constants {

	public static final String LOG_FILE = "XCL.log";
	public static final String CONTEXT_FILE = "XCL.ctx";
	public static final String SETTING_FILE = ".xcl";
	public static final String CMD_TITLE = "XCL";
	public static final String VERSION_PROMPT = " " + CMD_TITLE + " (version 1.1) was created by Van in 2017/10/18";
	public static final String IN_PROMPT = " > ";
	public static final String OUT_PROMPT = " < ";
	public static final String INFO_PROMPT = " # ";
	public static final String ERROR_PROMPT = " - ";
	public static final String ICON_IMAGE_PATH = "img/icon2.png";
	public static final String COMMONT_PREFIX = "//";
	public static final String PARAS_PREFIX = "-";
	public static final String PARAS_SPLITER = "=";
	public static final String PARAS_DELIMETER = "~";
	public static final String PARAS_DEFAULT = "~";
	public static final String PARAS_COMMAND = "@p";
	public static final String RUNCRAFT_COMMAND = "@r";
	public static final String RUNFILE_COMMAND = "@rf";
	public static final String REMOVE_COMMAND = "remove";
	public static final String EDIT_COMMAND = "edit";
	public static final String ECHO_COMMAND = "echo";
	public static final String CRAFT_COMMAND = "craft";
	public static final String ESC = ":esc";
	public static final Font DEFAULT_FONT = XCLUtils.getDefaultFont(Font.PLAIN, 15);
	
}
