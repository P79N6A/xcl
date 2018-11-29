package van.util.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.alibaba.fastjson.JSONArray;

public class JsonArray extends Json implements List<Object>, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8943371151800781650L;
	
	private Object impl = new JSONArray();
	
	protected Object impl() {
		return impl;
	}
	
	protected void impl(Object impl) {
		this.impl = impl;
	}
	
	public int size() {
		return ((JSONArray)impl).size();
	}
	
	public Object get(int index) {
		return ((JSONArray)impl).get(index);
	}
	
	public boolean add(Object object) {
		return ((JSONArray)impl).add(object);
	}
	
	public boolean addAll(Collection<? extends Object> c) {
		return ((JSONArray)impl).addAll(c);
	}
	
	public JsonArray clone() {
		Object clone = ((JSONArray)impl).clone();
		JsonArray array = new JsonArray();
		array.impl(clone);
		return array;
	}

	@Override
	public boolean isEmpty() {
		return ((JSONArray)impl).isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return ((JSONArray)impl).contains(o);
	}

	@Override
	public Iterator<Object> iterator() {
		return ((JSONArray)impl).iterator();
	}

	@Override
	public Object[] toArray() {
		return ((JSONArray)impl).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return ((JSONArray)impl).toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return ((JSONArray)impl).remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return ((JSONArray)impl).containsAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		return ((JSONArray)impl).addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return ((JSONArray)impl).removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return ((JSONArray)impl).retainAll(c);
	}

	@Override
	public void clear() {
		((JSONArray)impl).clear();
	}

	@Override
	public Object set(int index, Object element) {
		return ((JSONArray)impl).set(index, element);
	}

	@Override
	public void add(int index, Object element) {
		((JSONArray)impl).add(index, element);
	}

	@Override
	public Object remove(int index) {
		return ((JSONArray)impl).remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return ((JSONArray)impl).indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return ((JSONArray)impl).lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return ((JSONArray)impl).listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return ((JSONArray)impl).listIterator(index);
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return ((JSONArray)impl).subList(fromIndex, toIndex);
	}
	
	public String toString() {
		return ((JSONArray)impl).toString();
	}
}
