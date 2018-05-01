package _sam.random.codes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileTreeTextParser {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Dir dir = new Dir("root");
        
        Map<Integer, Dir> map = new HashMap<>(); 
        
        try(InputStream is = Files.newInputStream(Paths.get("D:\\movies_Unchecked\\_torrent\\_anime\\anime-names.txt"));
                Scanner sc = new Scanner(is , "utf-16")) {
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                int count = count(s);
                if(count == 0)
                    map.put(0, dir.add(s));
                else
                    map.put(count, map.get(count - 1).add(s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try(OutputStream is = new FileOutputStream(new File("2.txt"));
                PrintStream ps = new PrintStream(is, true, "utf-16")) {
            System.setOut(ps);
            dir.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static int count(String name) {
        int count = 0;
        
        int index = name.indexOf('|');
        
        while(index > 0) {
            count++;
            index = name.indexOf('|', index + 1);
        }
            
        return count;
    }

}

class Dir {
    final String name ;
    private List<Dir> dirs;

    public Dir(String name) {
        this.name = name;
    }
    public void print() {
        System.out.println(name);
        if(dirs == null)
            return;
        for (Dir d : dirs) 
            d.print();
    }

    public Dir add(String line) {
        return add(new Dir(line));
    }
    public Dir add(Dir d) {
        if(dirs == null)
            dirs = new ArrayList<>();
        
        dirs.add(d);
        return d;
    }

    @Override
    public String toString() {
        return "Dir [name=" + name + "]";
    }
}

