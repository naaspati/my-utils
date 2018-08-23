package sam.fileutils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

public final class FileNameSanitizer {
    public static final double VERSION = 1.22;
    private Charset charset;
    
    public FileNameSanitizer() {
    	this(Charset.forName("utf-8"));
	}
    public FileNameSanitizer(Charset charset) {
		this.charset = charset;
	}
    public Charset getCharset() {
		return charset;
	}
    public void setCharset(Charset charset) {
		this.charset = charset;
	}
	public  void multipleSpacesToNullChars(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ' || chars[i] == '\0')
                while (++i < chars.length && (chars[i] == ' ' || chars[i] == '\0')) chars[i] = '\0';
        }
    }

    /**
     * replace each windows reserved char in chars with null char \0
     * 
     * @param chars
     */
    public void replaceWindowReservedChars(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            if (Arrays.binarySearch(WINDOWS_RESERVED_CHARS, chars[i]) >= 0)
                chars[i] = chars[i] == ':' ? ' ' : '\0';
        }
    }

    /**
     * replace char with simple space (' ') if Character.isWhitespace(char),
     * 
     * @param chars
     */
    public void removeInvalidSpaceChars(char[] chars) {
        for (int i = 0; i < chars.length; i++)
            if (chars[i] != ' ' && Character.isWhitespace(chars[i]))
                chars[i] = '\0';
    }

    /**
     * move null chars end to end <br>
     * e.g. <br>
     * before {' ', '1',' ', ' ', ' ', '2'} <br>
     * after {' ', '1',' ', '2', ' ', ' '} <br>
     */
    public void arrangeChars(char[] chars) {
        char[] chars2 = new char[chars.length];

        int i = 0;
        for (char c : chars) {
            if (c != '\0')
                chars2[i++] = c;
        }

        i = 0;
        for (char c : chars2)
            chars[i++] = c;
    }

    /**
     * remove trailing spaces and '.' and created the string
     * 
     * @param chars
     * @return
     */
    public String trimAndCreate(char[] chars) {
        if (chars.length == 0)
            return "";

        int end = chars.length - 1;
        for (; end >= 0; end--) {
            if (chars[end] != '\0' && chars[end] != ' ' && chars[end] != '.')
                break;
        }

        if (end < 0)
            return "";

        int start = 0;
        if (chars[0] == '\0' || chars[0] == ' ') {
            for (; start < end; start++) {
                if (chars[start] != '\0' && chars[start] != ' ')
                    break;
            }
        }
        return new String(chars, start, end + 1 - start);
    }

    /*
     * this is sorted
     */
    private static final char[] WINDOWS_RESERVED_CHARS = { '"', '*', '/', ':', '<', '>', '?', '\\', '|' };
    static  {
    	Arrays.sort(WINDOWS_RESERVED_CHARS);
    }

    
    public static String removeInvalidCharsFromFileName(String name) {
    	return removeInvalidCharsFromFileName(name, Charset.forName("utf-8"));
    }
    
    private static FileNameSanitizer remover;
    public static String removeInvalidCharsFromFileName(String name, Charset charset) {
        if (name == null)
            throw new NullPointerException("Name: " + name);

        char[] chars = name.toCharArray();
        
        if(remover == null)
        remover = new FileNameSanitizer(charset);

        remover.removeUnmappableChars(chars);
        remover.replaceWindowReservedChars(chars);
        remover.removeInvalidSpaceChars(chars);
        remover.arrangeChars(chars);
        String str = remover.trimAndCreate(chars);

        if (str.isEmpty())
            throw new NullPointerException(
                    "at start mangaName: " + name + " and after formatting mangaName is empty a string");

        return str;
    }

	public void removeUnmappableChars(char[] chars) {
		CharsetEncoder encoder = charset.newEncoder();
		
		for (int i = 0; i < chars.length; i++) {
			if(!encoder.canEncode(chars[i]))
				chars[i] = ' ';
		}
	}
}
