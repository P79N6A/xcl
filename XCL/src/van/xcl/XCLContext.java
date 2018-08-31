package van.xcl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import van.xcl.util.json.Json;
import van.xcl.util.json.JsonObject;

import van.util.CommonUtils;

public class XCLContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private String path;
	private JsonObject object = null;
	private transient XCLHandler handler = null;
	private transient XCLCraftStorage craftStorage = null;
	public XCLContext() {
	}
	private Object getVar(String key, JsonObject par) {
		int idx = key.indexOf(".");
		if (idx > 0) {
			String parKey = key.substring(0, idx);
			Object parObj = par.get(parKey);
			if (parObj != null) {
				if (CommonUtils.isBasicObject(parObj)) {
					return String.valueOf(parObj);
				} else {
					if (parObj instanceof JsonObject) {
						return getVar(key.substring(idx + 1), (JsonObject) parObj);
					}
				}
			}
			return null;
		} else {
			Object obj = par.get(key);
			return obj;
		}
	}
	public JsonObject getObject() {
		if (object == null) {
			object = new JsonObject();
		}
		return object;
	}
	public boolean containsVar(String key) {
		return getObject().containsKey(key);
	}
	public void setVar(String key, Object value) {
		getObject().put(key, value);
	}
	public Object getVar(String key) {
		return getVar(key, getObject());
	}
	public boolean removeVar(String key) {
		if (getObject().containsKey(key)) {
			getObject().remove(key);
			return true;
		}
		return false;
	}
	public Map<String, Object> getDataMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : getObject().entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			map.put(name, value);
		}
		return map;
	}
	public XCLVar getXCLVar(String key) {
		Object obj = getVar(key);
		if (obj != null) {
			return new XCLVar(obj);
		}
		return null;
	}
	public String resolveVar(String input) {
		if (input != null && !"".equals(input.trim())) {
			input = input.trim();
			Object obj = getVar(input);
			if (obj != null && CommonUtils.isBasicObject(obj)) {
				return String.valueOf(obj);
			} else if (obj != null && obj instanceof Json) {
				return ((Json)obj).toJSONString();
			} else {
				return input;
			}
		}
		return "";
	}
	public XCLCraftStorage getCraftStorage() {
		if (craftStorage == null) {
			craftStorage = new XCLCraftStorage();
		}
		return craftStorage;
	}
	public Map<String, String> getCrafts() {
		return getCraftStorage().getCrafts();
	}
	public void setCraft(String name, String craft) {
		getCraftStorage().setCraft(name, craft);
	}
	public String getCraft(String name) {
		return getCraftStorage().getCraft(name);
	}
	public boolean removeCraft(String name) {
		return getCraftStorage().removeCraft(name);
	}
	public boolean containsCraft(String name) {
		return getCraftStorage().containsCraft(name);
	}
	public XCLHandler getHandler() {
		return handler;
	}
	public XCLContext setHandler(XCLHandler handler) {
		this.handler = handler;
		return this;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return this.path;
	}
	public XCLContext clone() {
		XCLContext c = new XCLContext();
		c.object = (JsonObject) getObject().clone();
		c.path = this.path;
		c.handler = this.handler;
		return c;
	}
}
