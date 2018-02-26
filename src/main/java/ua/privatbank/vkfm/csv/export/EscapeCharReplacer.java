package ua.privatbank.vkfm.csv.export;

/**
 * @author evgeniy.pismenny on 26.02.18 16:15.
 */
public class EscapeCharReplacer {

	private final char replacement;
	private final byte[] escChMap;

	public EscapeCharReplacer(char[] escChars, char replacement) {
		this(replacement, initEscChMap(escChars));
	}

	private EscapeCharReplacer(char replacement, byte[] escChMap) {
		this.replacement = replacement;
		this.escChMap = escChMap;
	}

	private static byte[] initEscChMap(char[] escChars) {
		String s = String.valueOf(escChars);
		byte[] arrMap = new byte[Character.MAX_VALUE + 1];
		s.chars().forEach(i -> arrMap[i] = 1);
		return arrMap;
	}


	public String replaceAll(String str) {
		char[] chars = str.toCharArray();
		for (int k = 0; k < chars.length; k ++) {
			char ch = chars[k];
			chars[k] = escChMap[ch] == 1 ? replacement : ch;
		}
		return String.valueOf(chars);
	}
}
