package van.xcl;

import java.awt.Color;
import java.awt.Font;

public class XCLConstants {

	// System properties
	public static final int DEFAULT_PORT = 40056;
	public static final int DEFAULT_PORT_OFFSET = 100;
	public static final long HEALTH_CHECK_PERIOD = 5000L;
	
	// Version properties
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
	
	// Command properties
	public static final String BUILTIN_VAR_PERFIX = "@";
	public static final String BUILTIN_VAL_PERFIX = "&";
	public static final String BUILTIN_DEF_PERFIX = ":";
	public static final String BUILTIN_PARA_SIGN = String.valueOf((char)127);
	public static final String COMMONT_PREFIX = "//";
	public static final String PARAS_PREFIX = "-";
	public static final String PARAS_SPLITER = "=";
	public static final String PARAS_DELIMETER = ";" + BUILTIN_PARA_SIGN;
	public static final String PARAS_DEFAULT = "~";
	public static final String COMMAND_PARAS = "@set_paras";
	public static final String COMMAND_RUN_CRAFT = "@run_craft";
	public static final String COMMAND_RUN_CRAFT_FILE = "@run_craft_file";
	public static final String COMMAND_REMOVE = "remove";
	public static final String COMMAND_EDIT = "edit";
	public static final String COMMAND_ECHO = "echo";
	public static final String COMMAND_CRAFT = "craft";
	public static final String TERMINATE_TAG = ";";
	public static final String ESC = ":esc";
	
	// Configuration file properties
	public static final String CONSTS_VAR_ENCODE_NAME = BUILTIN_VAR_PERFIX + "encoding";
	public static final String DEFAULT_CHARSET_NAME = "GBK";
	public static final String CRAFT_FILE_PATH = "crafts";
	public static final String CRAFT_FILE_EXT = ".craft";
	
	// Theme properties
	public static final Font DEFAULT_FONT = XCLUtils.getDefaultFont(Font.PLAIN, 15);
	public static final Color backgroundColor = new Color(39, 40, 34);
	public static final Color foregroundColor = new Color(248, 248, 242);
	public static final Color selectionColor = new Color(73, 72, 65);
	// public static final Color caretColor = new Color(255, 255, 255);
	public static final Color caretColor = new Color(0, 255, 255);
	public static final Color promptColor = new Color(128, 128, 128);
	public static final Color errorColor = new Color(249, 38, 101);
	public static final Color normalColor = new Color(248, 248, 242);
	public static final Color keyColor = new Color(0, 255, 255);
	public static final Color dynamicKeyColor = new Color(255, 255, 0);
	public static final Color commentColor = new Color(117, 113, 94);
	public static final Color trackColor = new Color(128, 128, 128);
	
}
