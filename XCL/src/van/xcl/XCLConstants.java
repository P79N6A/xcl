package van.xcl;

import java.awt.Color;
import java.awt.Font;

public class XCLConstants {

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
	
	public static final Color backgroundColor = new Color(39, 40, 34);
	public static final Color foregroundColor = new Color(248, 248, 242);
	public static final Color selectionColor = new Color(73, 72, 62);
	public static final Color promptColor = new Color(128, 128, 128);
	public static final Color errorColor = new Color(255, 0, 0);
	public static final Color normalColor = new Color(248, 248, 242);
	public static final Color keyColor = new Color(0, 255, 255);
	public static final Color commentColor = new Color(128, 128, 128);
	public static final Color trackColor = new Color(128, 128, 128);
	
//	public static final Color backgroundColor = Color.white;
//	public static final Color foregroundColor = Color.black;
//	public static final Color selectionColor = Color.lightGray;
//	public static final Color promptColor = Color.gray;
//	public static final Color errorColor = Color.red;
//	public static final Color normalColor = Color.black;
//	public static final Color keyColor = Color.blue;
//	public static final Color commentColor = Color.gray;
//	public static final Color trackColor = Color.white;
	
	
}
