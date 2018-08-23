package _sam.random.codes;

import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import sam.console.ANSI.FOREGROUND;

public class ColorMethods {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        JCheckBox p = new JCheckBox("private");
        JCheckBox s = new JCheckBox("static");
        p.setSelected(true);

        JOptionPane.showMessageDialog(null, new Object[] {p, s}, "choose", JOptionPane.QUESTION_MESSAGE); 

        String format = (p.isSelected() ? "private " : "public ")+(s.isSelected() ? "static " : "")+"String %s(Object obj) {return \"\\u001b[%dm\"+obj+\"\\u001b[0m\";}%n";

        for (Field f : FOREGROUND.class.getDeclaredFields())
            System.out.printf(format, f.getName().toLowerCase(), f.get(null));

        System.exit(0);
    }


}
