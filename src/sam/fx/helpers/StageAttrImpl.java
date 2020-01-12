package sam.fx.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Properties;

import javafx.stage.Stage;

class StageAttrImpl {
	private static double height = -1, width = -1, x = -1, y = -1;
	
	private final Path saveDir;
	private final StageAttr sizer;

	public StageAttrImpl(Path saveDir, StageAttr sizer) {
		this.saveDir = saveDir;
		this.sizer = sizer;
	}

	public void set(int defaultWidth, int defaultHeight) {
		Stage s = sizer.stage();
		try {
			Path p = path();
			if(Files.notExists(p)) {
				s.setWidth(defaultWidth);
				s.setHeight(defaultHeight);
			} else {
				Properties props = new Properties();
				props.load(Files.newBufferedReader(p));
				double w = Double.parseDouble(props.getProperty("width"));
				double h = Double.parseDouble(props.getProperty("height"));
				double x = Double.parseDouble(props.getProperty("x"));
				double y = Double.parseDouble(props.getProperty("y"));
				
				s.setWidth(w);
				s.setHeight(h);
				s.setX(x);
				s.setY(y);
				
				updateStore(s);
			}
		} catch (Exception e) {
			System.err.println("failed read: "+path());
			e.printStackTrace();
			
			s.setWidth(defaultWidth);
			s.setHeight(defaultHeight);
		}
	}

	private void updateStore(Stage s) {
		width = s.getWidth();
		height = s.getHeight();
		x = s.getX();
		y = s.getY();
	}

	private Path path() {
		Path p = saveDir;
		p = (p == null ? Paths.get(".") : p).resolve(sizer.getClass().getName().concat(".stage.size"));
		return p;
	}

	public void update() {
		try {
			Stage s = sizer.stage();
			
			if(height < 0 || width < 0 || x < 0 || y < 0 || width != s.getWidth() || height != s.getHeight()) {
				Properties props = new Properties();
				updateStore(s);
				props.setProperty("width", Double.toString(width));
				props.setProperty("height", Double.toString(height));
				props.setProperty("x", Double.toString(x));
				props.setProperty("y", Double.toString(y));
				props.store(Files.newOutputStream(path(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), LocalDateTime.now().toString());
			}
		} catch (IOException e) {
			System.err.println("failed to save: "+path());
			e.printStackTrace();
		}
	}

}
