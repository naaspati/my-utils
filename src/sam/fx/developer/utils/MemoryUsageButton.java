package sam.fx.developer.utils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sam.fx.helpers.FxHBox;
import sam.nopkg.Junk;

public class MemoryUsageButton extends Button implements EventHandler<ActionEvent> {
	
	public MemoryUsageButton() {
		super("mem use");
	}
	
	public MemoryUsageButton(String text, Node graphic) {
		super(text, graphic);
	}

	public MemoryUsageButton(String text) {
		super(text);
	}

	{
		setOnAction(this);
	}

	@Override
	public void handle(ActionEvent event) {
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.initModality(Modality.APPLICATION_MODAL);
		
		Label top = new Label("Memory Use");
		top.setMaxWidth(Double.MAX_VALUE);
		top.setStyle("-fx-font-size:1.7em;-fx-background-color:black;-fx-text-fill:white;-fx-padding:20 0 20 5;");
		TextArea center = new TextArea(Junk.memoryUsage());
		center.setEditable(false);
		
		BorderPane.setMargin(center,new Insets(10, 0, 0, 0));
		Button gc = new Button("GC");
		gc.setOnAction(e -> {
			System.gc();
			Platform.runLater(() -> center.setText(Junk.memoryUsage()));
		});
		BorderPane root = new BorderPane(center, top, null, FxHBox.buttonBox(gc), null);
		root.setStyle("-fx-font-family:Consolas;");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
