package van.xcl.util.json;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

public class JsonObject extends Json implements Map<String, Object>, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8942334709316977126L;
	
	private Object impl = new JSONObject();

	protected Object impl() {
		return impl;
	}

	protected void impl(Object impl) {
		this.impl = impl;
	}
	
	public String toString() {
		return ((JSONObject)impl).toString();
	}
	
	public JsonObject clone() {
		Object clone = ((JSONObject)impl).clone();
		JsonObject object = new JsonObject();
		object.impl(clone);
		return object;
	}

	@Override
	public int size() {
		return ((JSONObject)impl).size();
	}

	@Override
	public boolean isEmpty() {
		return ((JSONObject)impl).isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return ((JSONObject)impl).containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return ((JSONObject)impl).containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return ((JSONObject)impl).get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return ((JSONObject)impl).put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return ((JSONObject)impl).remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		((JSONObject)impl).putAll(m);
	}

	@Override
	public void clear() {
		((JSONObject)impl).clear();
	}

	@Override
	public Set<String> keySet() {
		return ((JSONObject)impl).keySet();
	}

	@Override
	public Collection<Object> values() {
		return ((JSONObject)impl).values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return ((JSONObject)impl).entrySet();
	}

}
