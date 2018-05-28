package van.xcl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import van.util.CommonUtils;

public class XCLContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private String path;
	private JSONObject object = null;
	private Map<String, String> crafts = null;
	private transient XCLHandler handler = null;
	public XCLContext() {
	}
	private Object getVar(String key, JSONObject par) {
		int idx = key.indexOf(".");
		if (idx > 0) {
			String parKey = key.substring(0, idx);
			Object parObj = par.get(parKey);
			if (parObj != null) {
				if (CommonUtils.isBasicObject(parObj)) {
					return String.valueOf(parObj);
				} else {
					if (parObj instanceof JSONObject) {
						return getVar(key.substring(idx + 1), (JSONObject) parObj);
					}
				}
			}
			return null;
		} else {
			Object obj = par.get(key);
			return obj;
		}
	}
	public JSONObject getObject() {
		if (object == null) {
			object = new JSONObject();
		}
		return object;
	}
	public Map<String, String> getCrafts() {
		if (crafts == null) {
			crafts = new HashMap<String, String>();
		}
		return crafts;
	}
	public void setCraft(String name, String craft) {
		getCrafts().put(name, craft);
	}
	public String getCraft(String name) {
		return getCrafts().get(name);
	}
	public boolean removeCraft(String name) {
		if (getCrafts().containsKey(name)) {
			getCrafts().remove(name);
		}
		return false;
	}
	public boolean containsCraft(String name) {
		return getCrafts().containsKey(name);
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
			} else if (obj != null && obj instanceof JSON) {
				return ((JSON)obj).toJSONString();
			} else {
				return input;
			}
		}
		return "";
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
		c.object = (JSONObject) getObject().clone();
		c.crafts = new HashMap<String, String>(getCrafts());
		c.path = this.path;
		c.handler = this.handler;
		return c;
	}
}
