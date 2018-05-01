package _sam.random.codes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sam.console.ANSI;
import sam.swing.SwingUtils;

public class ExtractFxGraphVariableNames {
    public static void main(String[] args) throws IOException {
        extract();
    }

    public static void extract() throws IOException {
        File file = SwingUtils.filePathInputOptionPane("path to file");
        if(file == null) {
            System.out.println(ANSI.red("CANCELLED"));
            return;
        }
        
        Pattern ptrn = Pattern.compile("(\\w+)\\s+id\\s+(\\w+)");
        Files.lines(file.toPath())
        .forEach(line -> {
            Matcher m = ptrn.matcher(line);
            while(m.find()) {
                System.out.printf("@FXML private %s %s;\n", m.group(1), m.group(2));
            }
        });
        
        System.out.println(ANSI.green("\n\nDONE"));
    }
}
