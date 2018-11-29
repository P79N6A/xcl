package van.util.json;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class Json implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1806831739165122718L;
	
	protected abstract Object impl();
	
	protected abstract void impl(Object impl);
	
	public static final Json parse(String text) {
		JSON json = (JSON) JSON.parse(text);
		if (json instanceof JSONArray) {
			JsonArray arr = new JsonArray();
			arr.impl(json);
			return arr;
		} else if (json instanceof JSONObject) {
			JsonObject obj = new JsonObject();
			obj.impl(json);
			return obj;
		}
		return null;
	}
	
	public static final JsonArray parseArray(String text) {
		JSONArray array = JSON.parseArray(text);
		JsonArray arr = new JsonArray();
		arr.impl(array);
		return arr;
	}
	
	public static final JsonObject parseObject(String text) {
		JSONObject object = JSON.parseObject(text);
		JsonObject obj = new JsonObject();
		obj.impl(object);
		return obj;
	}
	
	public static final String toJSONString(Object object) {
		if (object instanceof Json) {
			return JSON.toJSONString(((Json)object).impl());
		}
		return JSON.toJSONString(object);
	}
	
	public static final String toJSONString(Object object, boolean prettyFormat) {
		if (object instanceof Json) {
			return JSON.toJSONString(((Json)object).impl(), prettyFormat);
		}
		return JSON.toJSONString(object, prettyFormat);
	}
	
	public String toJSONString() {
		return toJSONString(this, false);
	}
	
}
