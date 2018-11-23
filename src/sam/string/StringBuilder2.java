package sam.string;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Formatter;
import java.util.Objects;
import java.util.stream.IntStream;

import sam.console.ANSI;
import sam.myutils.Checker;

public class StringBuilder2 implements Appendable, CharSequence, Externalizable {
    private static final long serialVersionUID = -5771932955099124983L;
    private StringBuilder sb;
    private transient Formatter fm;

    public StringBuilder2() {
        sb = new StringBuilder();
    }
    public StringBuilder2(int capacity) {
        sb = new StringBuilder(capacity);
    }
    public StringBuilder2(String str) {
        sb = new StringBuilder(str);
    }
    public StringBuilder2(CharSequence seq) {
        sb = new StringBuilder(seq);
    }
    public int length() {
        return sb.length();
    }
    public int hashCode() {
        return sb.hashCode();
    }
    public int capacity() {
        return sb.capacity();
    }
    public void ensureCapacity(int minimumCapacity) {
        sb.ensureCapacity(minimumCapacity);
    }
    public boolean equals(Object obj) {
        return sb.equals(obj);
    }
    
    @Override
    public IntStream chars() {
        return sb.chars();
    }
    public StringBuilder2 append(Object obj) {
        sb.append(obj);
        return this;
    }
    public StringBuilder2 append(String str) {
        sb.append(str);
        return this;
    }
    public StringBuilder2 append(StringBuffer sb) {
        this.sb.append(sb);
        return this;
    }
    public StringBuilder2 append(StringBuilder2 sb) {
        this.sb.append(sb.sb);
        return this;
    }
    public void trimToSize() {
        sb.trimToSize();
    }
    public void setLength(int newLength) {
        sb.setLength(newLength);
    }
    @Override
    public IntStream codePoints() {
        return sb.codePoints();
    }
    public StringBuilder2 append(CharSequence s) {
        sb.append(s);
        return this;
    }
    public StringBuilder2 append(CharSequence s, int start, int end) {
        sb.append(s, start, end);
        return this;
    }
    public StringBuilder2 append(char[] str) {
        sb.append(str);
        return this;
    }
    public StringBuilder2 append(char[] str, int offset, int len) {
        sb.append(str, offset, len);
        return this;
    }
    public StringBuilder2 append(boolean b) {
        sb.append(b);
        return this;
    }
    public StringBuilder2 append(char c) {
        sb.append(c);
        return this;
    }
    public StringBuilder2 append(int i) {
        sb.append(i);
        return this;
    }
    public StringBuilder2 append(long lng) {
        sb.append(lng);
        return this;
    }
    public StringBuilder2 append(float f) {
        sb.append(f);
        return this;
    }
    public StringBuilder2 append(double d) {
        sb.append(d);
        return this;
    }
    public StringBuilder2 aCodePoint(int codePoint) {
        sb.appendCodePoint(codePoint);
        return this;
    }
    public char charAt(int index) {
        return sb.charAt(index);
    }
    public StringBuilder2 delete(int start, int end) {
        sb.delete(start, end);
        return this;
    }
    public StringBuilder2 deleteCharAt(int index) {
        sb.deleteCharAt(index);
        return this;
    }
    public StringBuilder2 replace(int start, int end, String str) {
        sb.replace(start, end, str);
        return this;
    }
    public StringBuilder2 insert(int index, char[] str, int offset, int len) {
        sb.insert(index, str, offset, len);
        return this;
    }
    public int codePointAt(int index) {
        return sb.codePointAt(index);
    }
    public StringBuilder2 insert(int offset, Object obj) {
        sb.insert(offset, obj);
        return this;
    }
    public StringBuilder2 insert(int offset, String str) {
        sb.insert(offset, str);
        return this;
    }
    public StringBuilder2 insert(int offset, char[] str) {
        sb.insert(offset, str);
        return this;
    }
    public StringBuilder2 insert(int dstOffset, CharSequence s) {
        sb.insert(dstOffset, s);
        return this;
    }
    public StringBuilder2 insert(int dstOffset, CharSequence s, int start, int end) {
        sb.insert(dstOffset, s, start, end);
        return this;
    }
    public StringBuilder2 insert(int offset, boolean b) {
        sb.insert(offset, b);
        return this;
    }
    public int codePointBefore(int index) {
        return sb.codePointBefore(index);
    }
    public StringBuilder2 insert(int offset, char c) {
        sb.insert(offset, c);
        return this;
    }
    public StringBuilder2 insert(int offset, int i) {
        sb.insert(offset, i);
        return this;
    }
    public StringBuilder2 insert(int offset, long l) {
        sb.insert(offset, l);
        return this;
    }
    public StringBuilder2 insert(int offset, float f) {
        sb.insert(offset, f);
        return this;
    }
    public StringBuilder2 insert(int offset, double d) {
        sb.insert(offset, d);
        return this;
    }
    public int indexOf(String str) {
        return sb.indexOf(str);
    }
    public int indexOf(String str, int fromIndex) {
        return sb.indexOf(str, fromIndex);
    }
    public int codePointCount(int beginIndex, int endIndex) {
        return sb.codePointCount(beginIndex, endIndex);
    }
    public int lastIndexOf(String str) {
        return sb.lastIndexOf(str);
    }
    public int lastIndexOf(String str, int fromIndex) {
        return sb.lastIndexOf(str, fromIndex);
    }
    public StringBuilder2 reverse() {
        sb.reverse();
        return this;
    }
    public String toString() {
        return sb.toString();
    }
    public int offsetByCodePoints(int index, int codePointOffset) {
        return  sb.offsetByCodePoints(index, codePointOffset);
    }
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        sb.getChars(srcBegin, srcEnd, dst, dstBegin);
    }
    public void setCharAt(int index, char ch) {
        sb.setCharAt(index, ch);
    }
    public String substring(int start) {
        return sb.substring(start);
    }
    public CharSequence subSequence(int start, int end) {
        sb.subSequence(start, end);
        return this;
    }
    public String substring(int start, int end) {
        return sb.substring(start, end);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sb);
    }
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sb = (StringBuilder) in.readObject();
    }
    
    /* ******************************************
     * *****************  ANSI    ***************
     * ******************************************
     */


    public  StringBuilder2 black(Object obj){
        ANSI.black(sb, obj);
        return this;
    }
    public  StringBuilder2 red(Object obj){
        ANSI.red(sb, obj);
        return this;
    }
    public  StringBuilder2 green(Object obj){
        ANSI.green(sb, obj);
        return this;
    }
    public  StringBuilder2 yellow(Object obj){
        ANSI.yellow(sb, obj);
        return this;
    }
    public  StringBuilder2 blue(Object obj){
        ANSI.blue(sb, obj);
        return this;
    }
    public StringBuilder2 magenta(Object obj){
        ANSI.magenta(sb, obj);
        return this;
    }
    public  StringBuilder2 cyan(Object obj){
        ANSI.cyan(sb, obj);
        return this;
    }
    public  StringBuilder2 white(Object obj){
        ANSI.white(sb, obj);
        return this;
    }

    /* *****************************************
     * ************* Custom Methods ************
     * ***************************************** 
     */

    public StringBuilder2 appendJoined(String delimeter, CharSequence...elements) {
        Objects.requireNonNull(elements);
        
        if(elements.length == 0)
            return this;
        if(elements.length == 1)
            sb.append(elements[0]);
        else {
            for (int i = 0; i < elements.length - 1; i++) 
                sb.append(elements[i]).append(delimeter);

            sb.append(elements[elements.length - 1]);
        }
        return this;
    }

    public Formatter formatter() {
        return fm != null ? fm : (fm = new Formatter(sb));
    }
    public StringBuilder2 format(String format, Object...args) {
        formatter().format(format, args);
        return this;
    }
    /**
     * append new line
     * @return
     */
    public StringBuilder2 ln() {
        sb.append(System.lineSeparator());
        return this;
    }
    
    public StringBuilder2 repeat(CharSequence s, int times) {
        Objects.requireNonNull(s);
        
        while(times-- > 0) 
            sb.append(s);
        
        return this;
    }
    public StringBuilder2 repeat(char c, int times) {
        
        while(times-- > 0) 
            sb.append(c);
        
        return this;
    }
    
    private int mark = -1;
    /**
     * keep track of current length of builder;
     */
    public void mark() {
        mark = sb.length();
    }
    public int getMark() {
        return mark;
    }
    public void mark(int mark) {
        if(mark < 0)
            Checker.checkArgument(mark >= 0, "mark cannot be less than 0");
        if(mark > sb.length())
            throw new IndexOutOfBoundsException("max mark value: "+sb.length());
        
        this.mark = mark;
    }
    public void removeMark() {
        mark = -1;   
    }
    /**
     * set length of builder to mark
     */
    public void reset() {
        if(mark < 0)
            throw new IllegalStateException("mark not set");
        
        sb.setLength(mark);
    }
    
    
}
