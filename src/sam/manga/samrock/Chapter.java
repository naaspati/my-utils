package sam.manga.samrock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chapter implements Comparable<Chapter>{
    private String name; //chapter_name
    private double number; // chapter_number
    private boolean read; // isRead ?

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getNumber() {
        return number;
    }
    public void setNumber(double number) {
        this.number = number;
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean isRead) {
        this.read = isRead;
    }

    /**
     * isRead will be set to false 
     * @param chapterName
     */
    public Chapter(String chapterName) {
        name = chapterName;
        setRead(false);
    }
    public Chapter(String chapterName, boolean isRead) {
        chapterName = name;
        read = isRead;
    }
    private Chapter(DataInputStream in) throws IOException {
        number = in.readDouble();
        read = in.readBoolean();
        name = in.readUTF();
    }
    private void writeChapter(DataOutputStream out) throws IOException{
        out.writeDouble(number);
        out.writeBoolean(read);
        out.writeUTF(name);
    }
    @Override
    public int compareTo(Chapter c) {
        if(this.number == c.number)
            return this.name.compareToIgnoreCase(c.name);
        else
            return Double.compare(this.number, c.number);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Chapter))
            return false;

        return equals((Chapter)obj);
    }

    public boolean equals(Chapter chapter) {
        if(chapter == null)
            return false;

        return this.number == chapter.number && this.name.equals(chapter.name);
    }

    private static final Pattern PATTERN_TO_EXTRACT_DOUBLE_FROM_CHAPTER_NAME = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    /**
     * extract chapter_number from given chapter_name<br>
     * returns double value (in String) extracted from chapterName else null if not found    
     * @param chapterName
     * @return
     */
    public static String extractChapterNumber(String chapterName){
        Matcher m = PATTERN_TO_EXTRACT_DOUBLE_FROM_CHAPTER_NAME.matcher(chapterName);
        return m.find() ? m.group(1) : null;
    }

    /**
     * 
     * @param mangaFolderPath directory of the manga
     * @param chapters marked read in oldChapters will be marked read in newChapters (if newChapters contains that Chapter) 
     * @param isInIncreasingOrder  if true method will return newChapters in increasing order or else decreasing order 
     * @return newChapters, on case of mangaFolderPath doesn't exists or is empty, this method return a Chapter[0] 
     */
    public static Chapter[] listChaptersOrderedNaturally(File mangaFolderPath, Chapter[] oldChapters, boolean sortIncreasingly) {
        if(!mangaFolderPath.exists())
            return new Chapter[0];

        String[] names = mangaFolderPath.list();

        if(names.length == 0)
            return new Chapter[0];

        //one shot filters
        if(names.length == 1)
            return new Chapter[]{new Chapter(names[0], oldChapters == null || oldChapters.length == 0 ? false : oldChapters[0].isRead())};

        Chapter[] chapters = Stream.of(names).map(Chapter::new).toArray(Chapter[]::new);

        Arrays.sort(chapters);

        if(oldChapters != null && oldChapters.length != 0){
            List<String> old = Stream.of(oldChapters).filter(Chapter::isRead).map(Chapter::getName).collect(Collectors.toList());

            for (Chapter c : chapters) {
                if(old.contains(c.getName()))
                    c.setRead(true);
            }
        }

        if(!sortIncreasingly)
            reverse(chapters);

        return chapters;
    }

    public static byte[] chaptersToBytes(Chapter[] chapters){
        byte[] bytes = null;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos)) {

            if(chapters == null)
                chapters = new Chapter[0];

            out.writeInt(chapters.length);

            for (Chapter c : chapters)
                c.writeChapter(out);

            bytes = baos.toByteArray();
        }
        catch (IOException e) {
            throw new IllegalStateException("chaptersToBytes(Chapter[] chapters) failed");
        }
        return bytes;
    }

    public static Chapter[]  bytesToChapters(byte[] bytes) {
        Chapter[] chapters = null;

        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                DataInputStream in = new DataInputStream(bais)) {

            chapters = new Chapter[in.readInt()];

            for (int i = 0; i < chapters.length; i++) chapters[i] = new Chapter(in);
        }
        catch (IOException e) {
            throw new IllegalStateException("bytesToChapters(byte[] bytes) failed");
        }
        return chapters;
    }

    public static void reverse(Chapter[] chapters){
        if(chapters.length < 2)
            return;

        for (int i = 0; i < chapters.length/2; i++) {
            Chapter temp = chapters[i];
            chapters[i] = chapters[chapters.length - i - 1];
            chapters[chapters.length - i - 1] = temp;
        }
    }
}
