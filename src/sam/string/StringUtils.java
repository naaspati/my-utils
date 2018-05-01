package sam.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class StringUtils {
    public static final double VERSION = 0.004;

    public static boolean contains(String s, char c) {
        return s.indexOf(c) >= 0;	
    }

    public static String[] split(String s, char c) {
        Objects.requireNonNull(s);
        if(s.isEmpty())
            return new String[0];
        
        return split(s, c, new ArrayList<>()).toArray(new String[0]);
    }
    public static ArrayList<String> split(String s, char c, ArrayList<String> sink) {
        Objects.requireNonNull(s);
        if(s.isEmpty())
            return sink; 

        int start = 0;
        for (int end = 0; end < s.length(); end++) {
            if(s.charAt(end) == c) {
                sink.add(s.substring(start, end));
                start = end + 1;
            }
        }
        if(start != s.length())
            sink.add(s.substring(start, s.length()));
        return sink;
    }
    /**
     * joinIfNotEndsWithSeparator("anime", "sanam", "/") -> anime/sanam
     * joinIfNotEndsWithSeparator("anime/", "sanam", "/") -> anime/sanam
     * 
     * @param prefix
     * @param suffix
     * @param separator
     * @return
     */
    public static String joinIfNotEndsWithSeparator(String prefix, String suffix, String separator) {
        return prefix.endsWith(separator) ? prefix.concat(suffix) : (prefix.concat(separator).concat(suffix));
    }
    public static String camelCaseToSpacedString(String s) {
        if(s.isEmpty())
            return s;

        if(s.chars().allMatch(i -> !Character.isUpperCase(i))) 
            return s;

        StringBuilder sb = new StringBuilder(s.length()+16);
        int i = 0;
        while(!Character.isUpperCase(s.charAt(i++))) 
            sb.append(s.charAt(i-1));

        if(sb.length() != 0 && Character.isLowerCase(sb.charAt(sb.length() - 1)))
            sb.append(' ');
        sb.append(s.charAt(i - 1));

        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if(Character.isUpperCase(c) && i < s.length() - 1 && Character.isLowerCase(s.charAt(i + 1)))
                sb.append(' ');

            sb.append(c);
        }
        return sb.toString();
    }
    public static String join(Object...str) {
        if(str == null || str.length == 0) return null;
        if(str.length == 1) return String.valueOf(str[0]);
        if(str.length == 2) return str[0] != null && str[1] != null ? String.valueOf(str[0]).concat(String.valueOf(str[1])) : String.valueOf(str[0]) + str[1];

        StringBuilder sb = new StringBuilder();
        for (Object s : str) sb.append(s);
        return sb.toString();
    }


    public static String doubleToString(double d) {
        return doubleToString(d, Integer.MAX_VALUE);
    }
    public static String doubleToString(double d, int trimToPlaces) {
        if(d == (int)d)
            return String.valueOf((int)d);
        else {
            String s = String.valueOf(d);
            if(trimToPlaces == Integer.MAX_VALUE)
                return s;
            int index = s.indexOf('.') + trimToPlaces + 1;
            return s.substring(0, Math.min(index, s.length()));
        }
    }
    /**
     * times can be 0 or more <br>
     * in case of times = 0, returned value is a empty string.
     * 
     * @param s
     * @param times 
     * @return
     */
    public static String repeat(String s, int times) {
        Objects.requireNonNull(s);

        if(times == 0 || s.isEmpty())
            return "";
        if(times == 1)
            return s;
        if(times == 2)
            return s.concat(s);
        if(s.length() == 1)
            return String.valueOf(repeat(s.charAt(0), times));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) 
            sb.append(s);

        return sb.toString();
    }

    private static char[] repeat(char c, int times) {
        if(times == 0)
            return new char[0];
        if(times == 1)
            return new char[] {c};
        
        char[] cs = new char[times];
        Arrays.fill(cs, c);
        
        return cs;
    }

    public static StringBuilder repeat(CharSequence s, int times, StringBuilder sink) {
        Objects.requireNonNull(s);

        for (int i = 0; i < times; i++) 
            sink.append(s);

        return sink;
    }
    public static String remove(String from, char charater) {
        if(from.isEmpty())
            return from;
        if(!contains(from, charater))
            return from;

        char[] chars = from.toCharArray();
        int n = 0;
        for (char c : chars) {
            if(c != charater)
                chars[n++] = c;
        }
        return String.valueOf(chars, 0, n);
    }

}
