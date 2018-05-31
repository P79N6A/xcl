package van.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IoSerilizer<T> {

	private static final char delimeter = (char)0;
	private T object;
	private String string;
	
	public IoSerilizer(T object) {
		this.object = object;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			StringBuilder sb = new StringBuilder();
			for (byte b : bos.toByteArray()) {
				if (sb.length() > 0) {
					sb.append(delimeter);
				}
				sb.append(b);
			}
			this.string = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public IoSerilizer(String string) {
		this.string = string;
		try {
			String[] array = string.split(String.valueOf(delimeter));
			byte[] bytes = new byte[array.length];
			for (int i = 0 ; i < array.length ; i++) {
				byte b = (byte)Integer.parseInt(array[i]);
				bytes[i] = b;
			}
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
