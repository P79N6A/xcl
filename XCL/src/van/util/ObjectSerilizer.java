package van.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import van.util.codec.Encoder;

public class ObjectSerilizer<T> {

	private T object;
	private String string;
	private Encoder encoder = new Encoder();
	
	public ObjectSerilizer(T object) {
		this.object = object;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			this.string = encoder.encode(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ObjectSerilizer(String string) {
		this.string = string;
		try {
			byte[] bytes = encoder.decode(string);
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			this.object = (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getString() {
		return string;
	}
	
	public T getObject() {
		return object;
	}
	
}
