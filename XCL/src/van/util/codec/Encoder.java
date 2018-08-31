package van.util.codec;

import java.util.HashSet;
import java.util.Set;

/**
 * Encoder
 * @author YangYL
 */
public class Encoder {
	
	private static final int groupLength = 3;
	private static final int fixedGroupLength = 8;
	
	/* default dictionary */
	private char[] dictionary = {'0', '1', '2', '3', '4', '5', '6', '7'};
	/* default flags */
	private char[] flags = {'0', '8', '9'}; // '0' - 0 bits complement, '8' - 1 bits complement, '9' - 2 bits complement
	
	/**
	 * Encoder<br>
	 * Will use default dictionary and flags:
	 * <li>dictionary: {'0', '1', '2', '3', '4', '5', '6', '7'}
	 * <li>flags: {'0', '8', '9'}
	 */
	public Encoder() { }
	
	/**
	 * Encoder<br>
	 * Will use secureKey to generate customized dictionary and flags<br>
	 * @param secureKey secureKey is used for generating customized dictionary and flags
	 */
	public Encoder(String secureKey) {
		Set<Character> charSet = new HashSet<Character>();
		char[] chars = secureKey.toCharArray();
		for (int i = 0 ; i < chars.length ; i++) {
			char c1 = chars[i];
			char c2 = (char)(((byte)chars[i]) & 0x7f);
			if (isBaseChar(c1)) {
				charSet.add(c1);
			} else if (isBaseChar(c2)) {
				charSet.add(c2);
			}
		}
		Character[] charArray = charSet.toArray(new Character[0]);
		for (int i = 0 ; i < charArray.length && i < dictionary.length ; i++) {
			dictionary[i] = charArray[i];
		}
		for (int i = 0 ; i < charArray.length && i < flags.length ; i++) {
			flags[i] = charArray[i];
		}
	}
	
	/**
	 * Encodes given bytes
	 * @param bytes bytes to be encoded
	 * @return encoded string
	 */
	public String encode(byte[] bytes) {
		int length = bytes.length;
		int remainder = length % groupLength;
		char complement = remainder == 0 ? flags[0] : flags[groupLength - remainder];
		int fixedLength = remainder == 0 ? length : (length + (groupLength - remainder));
		byte[] fixedBytes = new byte[fixedLength];
		for (int i = 0 ; i < fixedLength ; i++) { // initial fixed bytes
			fixedBytes[i] = 0;
		}
		System.arraycopy(bytes, 0, fixedBytes, 0, length);
		int lineNumber = fixedLength / groupLength;
		int j = 0;
		char[] fixedChars = new char[lineNumber * fixedGroupLength + 1];
		fixedChars[j++] = complement;
		for (int line = 0, i = 0; line < lineNumber ; line++) {
			byte a = fixedBytes[i++];
			byte b = fixedBytes[i++];
			byte c = fixedBytes[i++];
			byte d1 = (byte)((a & 0xE0) >>> 5); // 11100000
			byte d2 = (byte)((a & 0x1c) >>> 2); // 00011100
			byte d3 = (byte)(((a & 0x03) << 1) | ((b & 0x80) >>> 7)); // 00000011 | 10000000
			byte d4 = (byte)((b & 0x70) >>> 4); // 01110000
			byte d5 = (byte)((b & 0x0e) >>> 1); // 00001110
			byte d6 = (byte)(((b & 0x01) << 2) | ((c & 0xc0) >>> 6)); // 00000001 | 11000000
			byte d7 = (byte)((c & 0x38) >>> 3); // 00111000
			byte d8 = (byte)((c & 0x07)); // 00000111
			fixedChars[j++] = dictionary[d1];
			fixedChars[j++] = dictionary[d2];
			fixedChars[j++] = dictionary[d3];
			fixedChars[j++] = dictionary[d4];
			fixedChars[j++] = dictionary[d5];
			fixedChars[j++] = dictionary[d6];
			fixedChars[j++] = dictionary[d7];
			fixedChars[j++] = dictionary[d8];
		}
		return new String(fixedChars);
	}
	
	/**
	 * Decodes given string
	 * @param str string to be decoded
	 * @return decoded bytes
	 */
	public byte[] decode(String str) {
		char[] fixedChars = str.toCharArray();
		if ((fixedChars.length - 1) % fixedGroupLength != 0) { // unexpected length of char array
			throw new IllegalArgumentException("Invalid encoded string!");
		}
		int j = 0;
		char complement = fixedChars[j++];
		if (-1 == indexOfFlags(complement)) { // undefined flag char found
			throw new IllegalArgumentException("Invalid encoded string!");
		}
		int lines = (fixedChars.length - 1) / fixedGroupLength;
		byte[] bytes = new byte[lines * groupLength];
		for (int line = 0, i = 0; line < lines ; line++) {
			char a = fixedChars[j++];
			char b = fixedChars[j++];
			char c = fixedChars[j++];
			char d = fixedChars[j++];
			char e = fixedChars[j++];
			char f = fixedChars[j++];
			char g = fixedChars[j++];
			char h = fixedChars[j++]; 
			bytes[i++] = (byte)((indexOfDictionary(a) << 5) | (indexOfDictionary(b) << 2) | (indexOfDictionary(c) >>> 1));
			bytes[i++] = (byte)((indexOfDictionary(c) << 7) | (indexOfDictionary(d) << 4) | (indexOfDictionary(e) << 1) | (indexOfDictionary(f) >>> 2));;
			bytes[i++] = (byte)((indexOfDictionary(f) << 6) | (indexOfDictionary(g) << 3) | indexOfDictionary(h));
		}
		byte[] fixedBytes = new byte[bytes.length - (complement == flags[0] ? 0 : (complement == flags[1] ? 1 : 2))];
		System.arraycopy(bytes, 0, fixedBytes, 0, fixedBytes.length);
		return fixedBytes;
	}
	
	private boolean isBaseChar(char c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	private int indexOfDictionary(char c) {
		return indexOfCharArray(dictionary, c);
	}
	
	private int indexOfFlags(char c) {
		return indexOfCharArray(flags, c);
	}
	
	private int indexOfCharArray(char[] array, char c) {
		for (int i = 0 ; i < array.length ; i++) {
			if (array[i] == c)
				return i;
		}
		return -1;
	}
	
	public static void main(String[] args) {
		Encoder encoder = new Encoder("acdeJk#-234D7");
		String str = "akony-groove-haha#@...zzzAbc";
		System.out.println("before: " + str);
		System.out.println("encoded: " + encoder.encode(str.getBytes()));
		System.out.println("decoded: " + new String(encoder.decode(encoder.encode(str.getBytes()))));
	}

}
