package sam.fx.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ErrorApp extends Application {
	public static String title;
	public static Throwable error;
	
	public static void set(String title, Throwable error) {
		ErrorApp.error = error;
		ErrorApp.title = title;
	}
	

	@Override
	public void start(Stage stage) throws Exception {
		if(title == null && error == null)
			throw new RuntimeException("no material specified to display");
		
		BorderPane root = new BorderPane();
		if(title != null){
			if(error == null) {
				Text text = new Text(title);
				text.setFont(Font.font("Consolas", 20));
				BorderPane.setMargin(text, new Insets(40, 20, 40, 20));
				root.setCenter(text);
			} else {
				Text text = new Text(title);
				text.setFont(Font.font("Consolas", 15));
				BorderPane.setMargin(text, new Insets(10, 5, 10, 5));
				root.setTop(text);
			}
		}
		
		if(error != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			
			String s = error.getMessage();
			if(s != null) {
				sw.append(s).append('\n')
				.append("-----------------------------------------\n");
			}
			
			error.printStackTrace(pw);
			
			TextArea ta = new TextArea(sw.toString());
			ta.setFont(Font.font("Consolas", 10));
			
			BorderPane.setMargin(ta, new Insets(5));
			root.setCenter(ta);
		}
		
		stage.setTitle("ERROR");
		stage.setScene(new Scene(root));
		stage.show();
	}
}
