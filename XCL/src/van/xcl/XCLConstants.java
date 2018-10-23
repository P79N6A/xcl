package van.xcl;

import java.awt.Color;
import java.awt.Font;

public class XCLConstants {

	public static final int DEFAULT_PORT = 40056;
	public static final int DEFAULT_PORT_OFFSET = 100;
	public static final String VERSION = "v1.1";
	public static final String OUT_FILE = "XCL.out";
	public static final String DEFAULT_CONTEXT_FILE = "XCL.ctx";
	public static final String SETTING_FILE = ".xcl";
	public static final String VERSION_PROMPT = "XCL(" + VERSION + ") created at 2017/10/18 by Van";
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
	public static final String PARAS_COMMAND = "__prepare_paras";
	public static final String RUNCRAFT_COMMAND = "__run_craft";
	public static final String RUNFILE_COMMAND = "__run_file";
	public static final String REMOVE_COMMAND = "remove";
	public static final String EDIT_COMMAND = "edit";
	public static final String ECHO_COMMAND = "echo";
	public static final String CRAFT_COMMAND = "craft";
	public static final String DISCONNECT_COMMAND = "disconnect";
	public static final String TERMINATE_TAG = ";";
	public static final String ESC = ":esc";
	public static final String CRAFT_FILE_PATH = "crafts";
	public static final String CRAFT_FILE_EXT = ".craft";
	public static final Font DEFAULT_FONT = XCLUtils.getDefaultFont(Font.PLAIN, 15);
	public static final long HEALTH_CHECK_PERIOD = 5000L;
	
	public static final Color backgroundColor = new Color(39, 40, 34);
	public static final Color foregroundColor = new Color(248, 248, 242);
	public static final Color selectionColor = new Color(255, 0, 0);
	public static final Color caretColor = Color.cyan;
	public static final Color promptColor = new Color(128, 128, 128);
	public static final Color errorColor = new Color(255, 0, 0);
	public static final Color normalColor = new Color(248, 248, 242);
	public static final Color keyColor = new Color(0, 255, 255);
	public static final Color dynamicKeyColor = new Color(255, 255, 0);
	public static final Color commentColor = new Color(128, 128, 128);
	public static final Color trackColor = new Color(128, 128, 128);
	
	//	public static final Color backgroundColor = Color.white;
	//	public static final Color foregroundColor = Color.black;
	//	public static final Color selectionColor = Color.lightGray;
	//	public static final Color caretColor = Color.black;
	//	public static final Color promptColor = Color.gray;
	//	public static final Color errorColor = Color.red;
	//	public static final Color normalColor = Color.black;
	//	public static final Color keyColor = Color.blue;
	//	public static final Color commentColor = Color.gray;
	//	public static final Color trackColor = Color.white;
	
	
}
