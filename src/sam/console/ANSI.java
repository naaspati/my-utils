package sam.console;

import java.util.Arrays;

import sam.myutils.System2;

public final class ANSI {
    public static final double VERSION = 1.2;

    private static boolean no_color = Boolean.valueOf(System2.lookup("sam.console.ANSI.no_color", "false"));
    public static void disable() {
    	no_color = true;
    }
    public static void enable() {
    	no_color = false;
    }
    public static boolean isDisabled(boolean disable) {
    	return no_color;
    }
    /**Reset / Normal all attributes off*/
    public static  final byte  RESET = 0;

    /**Bold or increased byteensity*/
    public static  final byte  BOLD_ON = 1;

    /**2 Fabyte (decreased byteensity) Not widely supported.*/
    public static  final byte FAbyte = 2;


    /**Italic: on Not widely supported. Sometimes treated as inverse.*/
    public static  final byte  ITALICS_ON = 3;

    /**Underline: Single*/
    public static  final byte  UNDERLINE_ON = 4;

    /**5 Blink: Slow less than 150 per minute*/
    public static  final byte BLINK_SLOW = 5;

    /**6 Blink: Rapid MS­DOS SYS? 150+ per minute? not widely supported*/
    public static  final byte BLINK_RAPID = 6;

    /**Image: Negative inverse or reverse swap foreground and background {@link "https://en.wikipedia.org/wiki/Reverse_video"}*/
    public static  final byte  INVERSE_ON = 7;

    /**8 Conceal Not widely supported.*/
    public static  final byte CONCEAL_NOT = 8;

    /**strikethrough on (Crossed­out Characters legible, but marked for deletion. Not widely supported.)*/
    public static  final byte  STRIKETHROUGH_ON = 9;

    /**10 Primary(default) font*/
    public static  final byte PRIMARY_FONT = 10;

    /**11–19 ­th alternate font Select the ­th alternate font (14 being the fourth alternate font, up to 19 being the 9th alternate font).*/

    /**20 Fraktur hardly ever supported*/
    public static  final byte FRAKTUR = 20;


    /**21 Bold: off or Underline: Double Bold off not widely supported? double underline hardly ever supported.*/
    public static  final byte BOLD_OFF_OR_UNDERLINE_DOUBLE_BOLD_OFF = 21;

    /**bold off (Normal color or byteensity Neither bold nor fabyte)*/
    public static  final byte  BOLD_OFF = 22;


    /**italics off (Not italic, not Fraktur)*/
    public static  final byte  ITALICS_OFF = 23;


    /**underline off (Underline: None Not singly or doubly underlined)*/
    public static  final byte  UNDERLINE_OFF = 24;

    /**25 Blink: off*/
    public static  final byte BLINK_OFF = 25;

    /**26 Reserved*/

    /**inverse off (Image: Positive)*/
    public static  final byte  INVERSE_OFF = 27;

    /**28 Reveal conceal off*/
    public static  final byte Reveal_conceal = 28;

    /**strikethrough off (Not crossed out)*/
    public static  final byte  STRIKETHROUGH_OFF = 29;

    public static interface FOREGROUND {
        byte BLACK = 30;
        byte RED = 31;
        byte GREEN = 32;
        byte YELLOW = 33;
        byte BLUE = 34;
        byte MAGENTA = 35;
        byte CYAN = 36;
        byte WHITE = 37;
    } 

    /**38 Reserved for extended set foreground color typical supported next arguments are 5;n where is color index (0..255) or 2;r;g;b where are red, green and blue color channels (out of 255)*/


    /**Default text color (foreground) implementation defined (according to standard)*/
    public static  final byte FOREGROUND_DEFAULT = 39;

    public static interface BACKGROUND {
        byte BLACK = 40;
        byte RED = 41;
        byte GREEN = 42;
        byte YELLOW = 43;
        byte BLUE = 44;
        byte MAGENTA = 45;
        byte CYAN = 46;
        byte WHITE = 47;
    }

    /**48 Reserved for extended set background color typical supported next arguments are 5;n where is color index (0..255) or 2;r;g;b where are red, green and blue color channels (out of 255)*/

    /**Default background color implementation defined (according to standard)*/	
    public static  final byte BACKGROUND_DEFAULT = 49;

    /**50 Reserved*/

    /**51 Framed*/
    public static  final byte FRAMED = 51;


    /**52 Encircled*/
    public static  final byte ENCIRCLED = 52;


    /**53 Overlined*/
    public static  final byte OVERLINED = 53;


    /**54 Not framed or encircled*/
    public static  final byte NOT_FRAMED = 54;


    /**55 Not overlined*/
    public static  final byte NOT_OVERLINED = 55;


    /**56–59 Reserved*/

    /**60 ideogram underline or right side line hardly ever supported*/
    public static  final byte IDEOGRAM_UNDERLINE_OR_RIGHT_SIDE_LINE = 60;


    /**61 ideogram double underline or double line on the right side hardly ever supported*/
    public static  final byte IDEOGRAM_DOUBLE_UNDERLINE_OR_DOUBLE_LINE_ON_THE_RIGHT_SIDE = 61;


    /**62 ideogram overline or left side line hardly ever supported*/
    public static  final byte IDEOGRAM_OVERLINE_OR_LEFT_SIDE_LINE = 62;


    /**63 ideogram double overline or double line on the left side hardly ever supported*/
    public static  final byte IDEOGRAM_DOUBLE_OVERLINE_OR_DOUBLE_LINE = 63;


    /**64 ideogram stress marking hardly ever supported*/
    public static  final byte IDEOGRAM_STRESS_MARKING = 64;


    /**65 ideogram attributes off hardly ever supported, reset the effects of all of 60–64*/
    public static  final byte IDEOGRAM_ATTRIBUTES_OFF = 65;

    /**Set foreground text color Black, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_BLACK = 90;
    /**Set foreground text color Red, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_RED = 91;
    /**Set foreground text color Green, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_GREEN = 92;
    /**Set foreground text color Yellow, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_YELLOW = 93;
    /**Set foreground text color Blue, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_BLUE = 94;
    /**Set foreground text color Magenta, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_MAGENTA = 95;
    /**Set foreground text color Cyan, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_CYAN = 96;
    /**Set foreground text color White, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_FOREGROUND_WHITE = 97;

    /**Set background Black, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_BLACK = 100;
    /**Set background Red, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_RED = 101;
    /**Set background Green, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_GREEN = 102;
    /**Set background Yellow, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_YELLOW = 103;
    /**Set background Blue, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_BLUE = 104;
    /**Set background Magenta, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_MAGENTA = 105;
    /**Set background Cyan, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_CYAN = 106;
    /**Set background White, high byteensity aixterm (not in standard) */
    public static  final byte HIGH_INTENSITY_BACKGROUND_WHITE = 107;

    /**
     * "\u001b["
     * <br><br>
     * e.g. <b><i> ANSI_START</b></i>  </b></i>  +  <b><i> FOREGROUND_BLACK</b></i>  </b></i>  +  <b><i> "\u001b[0m"_START</b></i>  </b></i>  +  <b>normal text</b></b></i>  </b></i>  +  <b><i> "\u001b[0m"      
     * 
     */
    public static  final String ANSI_START = "\u001b[";
    /**
     * "\u001b[0m"_START = 'm'<br> when color codes added to "\u001b[0m"_START is added before adding text 
     * <br><br>
     * e.g. <b><i> ANSI_START</b></i>  </b></i>  +  <b><i> FOREGROUND_BLACK</b></i>  </b></i>  +  <b><i> "\u001b[0m"_START</b></i>  </b></i>  +  <b>normal text</b></b></i>  </b></i>  +  <b><i> "\u001b[0m"      
     * 
     *   
     */
    public static  final char ANSI_START_CLOSE = 'm'; 
    /**
     * "\u001b[0m"
     *  <br><br>
     * e.g. <b><i> ANSI_START</b></i>  </b></i>  +  <b><i> FOREGROUND_BLACK</b></i>  </b></i>  +  <b><i> "\u001b[0m"_START</b></i>  </b></i>  +  <b>normal text</b></b></i>  </b></i>  +  <b><i> "\u001b[0m"	
     */
    public static  final String ANSI_CLOSE = "\u001b[0m";

    /**
     * this method wraps given string in ansi color codes,
     * <br> dont supply 0(RESET)  
     * @param string
     * @param ansiCodes
     */
    public static  String wrap(Object string, byte... ansiColorCodes){
        if(no_color) return String.valueOf(string);

        StringBuilder b = new StringBuilder(ANSI_START);
        for (byte b1 : ansiColorCodes) b.append(b1).append(';');
        b.append(ANSI_START_CLOSE)
        .append(string)
        .append("\u001b[0m");

        return b.toString();
    }

    private static String wrap(String prefix, Object value) {
        if(no_color) return String.valueOf(value);
        return prefix + value +  "\u001b[0m";
    }
    private static StringBuilder wrap(String prefix, Object value, StringBuilder sb) {
        if(no_color) return sb.append(value);
        return sb.append(prefix).append(value).append( "\u001b[0m");
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_BLACK}*/
    public static  String black(Object obj){
        return wrap("\u001b[30m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_RED}*/
    public static  String red(Object obj){
        return wrap("\u001b[31m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_GREEN}*/
    public static  String green(Object obj){
        return wrap("\u001b[32m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_YELLOW}*/
    public static  String yellow(Object obj){
        return wrap("\u001b[33m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_BLUE}*/
    public static  String blue(Object obj){
        return wrap("\u001b[34m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_MAGENTA}*/   
    public static  String magenta(Object obj){
        return wrap("\u001b[35m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_CYAN}*/
    public static  String cyan(Object obj){
        return wrap("\u001b[36m", obj);
    }

    /**equivalent to {@link #wrap(String, byte...)} with byte = {@link #FOREGROUND_WHITE}*/
    public static  String white(Object obj){
        return wrap("\u001b[37m", obj);
    }

    /**
     * b.append("\u001b[30m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     */
    public static  StringBuilder black(StringBuilder b, Object obj){
        return wrap("\u001b[30m",obj, b);
    }
    /**
     * b.append("\u001b[31m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     */
    public static  StringBuilder red(StringBuilder b, Object obj){
        return wrap("\u001b[31m",obj, b);
    }
    /**
     * b.append("\u001b[32m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     */
    public static  StringBuilder green(StringBuilder b, Object obj){
        return wrap("\u001b[32m",obj, b);
    }
    /**
     * b.append("\u001b[33m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     * @return received StringBuilder
     */
    public static  StringBuilder yellow(StringBuilder b, Object obj){
        return wrap("\u001b[33m",obj, b);
    }
    /**
     * b.append("\u001b[34m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     * @return received StringBuilder
     */
    public static  StringBuilder blue(StringBuilder b, Object obj){
        return wrap("\u001b[34m",obj, b);
    }
    /**
     * b.append("\u001b[35m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     * @return received StringBuilder
     */
    public static  StringBuilder magenta(StringBuilder b, Object obj){
        return wrap("\u001b[35m",obj, b);
    }
    /**
     * b.append("\u001b[37m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     * @return received StringBuilder
     */
    public static  StringBuilder cyan(StringBuilder b, Object obj){
        return wrap("\u001b[36m",obj, b);
    }
    /**
     * b.append("\u001b[37m").append(obj).append("\u001b[0m");
     * @param b
     * @param obj
     * @return received StringBuilder
     */
    public static  StringBuilder white(StringBuilder b, Object obj){
        return wrap("\u001b[37m",obj, b);
    }

    /**
     * create banner with<br>
     * 
     * calls with {@link #createBanner(String str, length = 50, symbol = '#', textColor = FOREGROUND_YELLOW, symbolColor =  = FOREGROUND_BLUE)}
     * 
     * @param str
     * @return
     */
    public static String createBanner(String text){
        return createBanner(text, 50, '#', FOREGROUND.YELLOW, FOREGROUND.BLUE);
    }
    public static String createBanner(String text,int textColor, int symbolColor){
        return createBanner(text, 50, '#', textColor, symbolColor);
    }
    public static String createUnColoredBanner(String text, int length, char symbol){
        return createBanner(text, length, symbol, -1, -1);
    }
    /**
     * length = 50, symbol = '#'
     * @param text
     * @return
     */
    public static String createUnColoredBanner(String text){
        return createBanner(text, 50, '#', -1, -1);
    }
    /**
     * 
     * @param text should be single line
     * @param length string length of single line
     * @param symbol char used to wrap the string
     * @param textColor FOREGROUND_{color} color to wrap the text (if u wish skip ansi coloring then pass this value -1)  
     * @param symbolColor FOREGROUND_{color} color to wrap the symbol (if u wish skip ansi coloring then pass this value -1)
     * @return
     */
    public static String createBanner(String text, int length, char symbol, int textColor, int symbolColor){
        if(text == null)
            text = "null";

        StringBuilder b = new StringBuilder();
        char[] symbols = new char[length];
        Arrays.fill(symbols, symbol);

        if(symbolColor != -1){
            b.append(ANSI_START)
            .append(symbolColor)
            .append(ANSI_START_CLOSE);
        }

        b.append(symbols);
        b.append("\r\n");

        int half = (length - text.length() - 2)/2;
        boolean lengthBool = half > 2;

        if(lengthBool)
            for (int i = 0; i < half; i++) b.append(symbol);

        if(symbolColor != -1)
            b.append("\u001b[0m");

        if(textColor != -1){
            b.append(ANSI_START)
            .append(textColor)
            .append(ANSI_START_CLOSE);
        }

        if(lengthBool){
            b.append(' ')
            .append(text)
            .append(' ');
        }
        else
            b.append(text);

        if(symbolColor != -1){
            b.append(ANSI_START)
            .append(symbolColor)
            .append(ANSI_START_CLOSE);
        }

        if(textColor != -1)
            b.append("\u001b[0m");

        if(symbolColor != -1){
            b.append(ANSI_START)
            .append(symbolColor)
            .append(ANSI_START_CLOSE);
        }

        if(lengthBool)
            for (int i = half; i < length - text.length() - 2; i++) b.append(symbol);

        b.append("\r\n");
        b.append(symbols);

        if(symbolColor != -1)
            b.append("\u001b[0m");

        return b.toString();
    }
    /**
     * pre created banner with text "FINISHED"
     */
    public static  final String FINISHED_BANNER = createBanner("FINISHED");
    /**
     * pre created banner with text "FAILED"
     */
    public static final String FAILED_BANNER = createBanner("FAILED");
}
