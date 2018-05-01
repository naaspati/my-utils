package sam.fileutils;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public void openFile(File file) throws IOException{
        Runtime.getRuntime().exec("explorer \""+file.getName()+"\"", null, file.getParentFile());
    }
    public void openFileNoError(File file){
        try {
            openFile(file);
        } catch (Exception e) {}
    }

    public void openFileLocationInExplorer(File file) throws IOException {
        if(file == null)
            throw new NullPointerException("path is null");

        Runtime.getRuntime().exec("explorer /select, \""+file.getName()+"\"", null, file.getParentFile());
    }

    public void openFileLocationInExplorerNoError(File file) {
        try {
            openFileLocationInExplorer(file);
        } catch (Exception e) {}
    }

}
