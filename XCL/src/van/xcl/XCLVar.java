package van.xcl;

import java.io.Serializable;
import java.math.BigDecimal;

import van.xcl.util.json.Json;
import van.xcl.util.json.JsonArray;
import van.xcl.util.json.JsonObject;

public class XCLVar implements Serializable {
	
	public enum Type {
		Null,
		String,
		Boolean,
		Numeric,
		JSONObject,
		JSONArray,
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Type type;
	// value
	private String string;
	private Boolean bool;
	private BigDecimal number;
	private JsonObject jsonObject;
	private JsonArray jsonArray;
	
	public XCLVar() {
		this.type = Type.Null;
	}
	
	public XCLVar(Object object) {
		this(object, false);
	}
	
	public XCLVar(Object object, boolean isString) {
		if (isString) {
			this.string = String.valueOf(object);
			this.type = Type.String;
		} else {
			if (object instanceof XCLVar) {
				clone(object);
			} else if (object instanceof String) {
				String string = (String) object;
				parseString(string);
			} else if (object instanceof JsonObject) {
				this.jsonObject = (JsonObject) object;
				this.type = Type.JSONObject;
			} else if (object instanceof JsonArray) {
				this.jsonArray = (JsonArray) object;
				this.type = Type.JSONArray;
			} else {
				parseString(String.valueOf(object));
			}
		}
	}
	
	private void parseString(String string) {
		Json json = parseJSON(string);
		BigDecimal number = parseNumber(string);
		Boolean bool = parseBoolean(string);
		if (json != null) {
			if (json instanceof JsonObject) {
				this.jsonObject = (JsonObject) json;
				this.type = Type.JSONObject;
			} else if (json instanceof JsonArray) {
				this.jsonArray = (JsonArray) json;
				this.type = Type.JSONArray;
			} else {
				this.type = Type.Null;
			}
		} else if (number != null) {
			this.number = number;
			this.type = Type.Numeric;
		} else if (bool != null) {
			this.bool = bool;
			this.type = Type.Boolean;
		} else {
			this.type = Type.String;
			this.string = string;
		}
	}
	
	private Json parseJSON(String string) {
		try {
			Json json = (Json) JsonObject.parse(string);
			return json;
		} catch (Exception e) { 
			return null;
		}
	}
	
	private BigDecimal parseNumber(String string) {
		try {
			return new BigDecimal(string);
		} catch (Exception e) { 
			return null;
		}
	}
	
	private Boolean parseBoolean(String string) {
		if ("true".equals(string) || "false".equals(string)) {
			return Boolean.valueOf(string);
		}
		return null;
	}
	
	public boolean isNull() {
		return Type.Null.equals(type);
	}
	
	public boolean isString() {
		return Type.String.equals(type);
	}
	
	public boolean isNumeric() {
		return Type.Numeric.equals(type);
	}
	
	public boolean isBoolean() {
		return Type.Boolean.equals(type);
	}
	
	public boolean isJsonObject() {
		return Type.JSONObject.equals(type);
	}

	public boolean isJsonArray() {
		return Type.JSONArray.equals(type);
	}
	
	public String getString() {
		return string;
	}
	
	public Boolean getBoolean() {
		return bool;
	}
	
	public BigDecimal getNumber() {
		return number;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public JsonArray getJsonArray() {
		return jsonArray;
	}
	
	public Type getType() {
		return type;
	}
	
	public void clone(Object obj) {
		this.type = ((XCLVar) obj).type;
		this.bool = ((XCLVar) obj).bool;
		this.number = ((XCLVar) obj).number;
		this.string = ((XCLVar) obj).string;
		this.jsonObject = ((XCLVar) obj).jsonObject;
		this.jsonArray = ((XCLVar) obj).jsonArray;
	}
	
	public String toString() {
		if (isNull()) {
			return null;
		} else if (isString()) {
			return getString();
		} else if (isBoolean()) {
			return getBoolean().toString();
		} else if (isNumeric()) {
			return getNumber().toPlainString();
		} else if (isJsonArray()) {
			return getJsonArray().toJSONString();
		} else if (isJsonObject()) {
			return getJsonObject().toJSONString();
		} else {
			return super.toString();
		}
	}
	
	public String toString(boolean captureSpace) {
		if (captureSpace) {
			return XCLUtils.captureSpace(toString());
		}
		return toString();
	}
	
}
