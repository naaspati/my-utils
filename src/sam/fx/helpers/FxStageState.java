package sam.fx.helpers;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import javafx.application.Platform;
import javafx.stage.Stage;

public class FxStageState {
	public final Path path;
	private double[] state;
	private boolean loaded;
	
	public FxStageState(Path path) {
		this.path = Objects.requireNonNull(path);
	}
	
	public void apply(Stage stage) {
		Objects.requireNonNull(stage);
		
		if(loaded)
			apply0(stage);
		
		if(Files.notExists(path))
			defaultApply(stage);
		else {
			try(FileChannel fc = FileChannel.open(path, READ)) {
				int bytes = 4 * Double.BYTES;
				ByteBuffer buf = ByteBuffer.allocate(bytes);
				if(fc.read(buf) < bytes)
					Platform.runLater(() -> defaultApply(stage));
				else {
					buf.flip();
					state = new double[]{
							buf.getDouble(),
							buf.getDouble(),
							buf.getDouble(),
							buf.getDouble()	,	
					};
					loaded = true;
					apply0(stage);
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Files.deleteIfExists(path);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Platform.runLater(() -> defaultApply(stage));
			}
		}
	}

	private void apply0(Stage stage) {
		stage.setX(state[0]);
		stage.setY(state[1]);
		stage.setWidth(state[2]);
		stage.setHeight(state[3]);
	}

	protected void defaultApply(Stage stage) {
	}
	
	public boolean save(Stage stage) throws IOException {
		Objects.requireNonNull(stage);
		 
		double[] state = {
				stage.getX(),
				stage.getY(),
				stage.getWidth(),
				stage.getHeight()		
		};
		
		if(Arrays.equals(this.state, state))
			return false;
		
		this.state = state;
		
		try(FileChannel fc = FileChannel.open(path, WRITE, CREATE, TRUNCATE_EXISTING)) {
			int bytes = 4 * Double.BYTES;
			ByteBuffer buf = ByteBuffer.allocate(bytes);
			for (double d : state) 
				buf.putDouble(d);
			
			buf.flip();
			fc.write(buf);
		}
		
		return true;
		
		
	}
}
