package sam.fx.transformer;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Test extends Application {
	public static void main(String[] args) throws IOException {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// https://unsplash.com/photos/Qmox1MkYDnY
		ImageView imageView = new ImageView(new Image(Paths.get("C:/Users/Sameer/Documents/MEGA/PHOTOS/mikhail-vasilyev-34524-2.jpg").toUri().toString(), true));

		Pane pane = new Pane(imageView);
		stage.setScene(new Scene(pane, 500, 500));

		stage.show();
		new FxTransformer(imageView, "imageView", stage, -500, 500).show();
	}

}
