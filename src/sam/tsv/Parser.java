package sam.tsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

import sam.myutils.MyUtilsCheck;
import sam.string.StringUtils;


abstract class Parser extends Escaper {
    private String nullReplacement;
    private int lineNumber = 0;
    private String line;
    private String[] lineArray;
    
    void parse(InputStream is, Charset charset, String nullReplacement, TsvParserOption...optionsA) throws IOException {
    	EnumSet<TsvParserOption> options =EnumSet.noneOf(TsvParserOption.class);
        if(MyUtilsCheck.isNotEmpty(optionsA)) {
        	for (TsvParserOption t : optionsA) 
				options.add(t);
        }
    	parse(is, charset, nullReplacement, options);
    }
    
    void parse(InputStream is, Charset charset, String nullReplacement, EnumSet<TsvParserOption> options) throws IOException {
        this.nullReplacement = nullReplacement;
        lineNumber = 0;
        
        try(InputStreamReader isr = new InputStreamReader(is, charset);
                BufferedReader reader = new BufferedReader(isr)) {
            
            if(options.contains(TsvParserOption.FIRST_ROW_IS_COLUMN_NAME)) {
                String firstLine = reader.readLine();

                if(firstLine == null)
                    throw new TsvException("file does not first row");
                setColumnsNames(processLine(firstLine));
            }
            reader.lines().forEach(l -> addRow(processLine(l)));
        } catch (Exception e) {
            throw new ParsingException("line("+lineNumber+") : "+line + " -> " + Arrays.toString(lineArray), e);
        }
    }
    private class ParsingException extends IOException {
        private static final long serialVersionUID = 1L;

        public ParsingException(String string, Exception e) {
            super(string, e);
        }
    }

    private String[] processLine(String l) {
        line = l;
        lineNumber++;
        lineArray = null;
        lineArray = StringUtils.split(l, '\t'); 
        unescape(lineArray);
        
        if(nullReplacement != null) {
            for (int i = 0; i < lineArray.length; i++) {
                if(Objects.equals(nullReplacement, lineArray[i]))
                    lineArray[i] = null;
            }
        }
        return lineArray;
    }
    abstract void addRow(String[] row);
    abstract void setColumnsNames(String[] values);
}
