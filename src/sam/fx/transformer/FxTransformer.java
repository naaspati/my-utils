package sam.fx.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class FxTransformer extends Stage {

	public FxTransformer(Node node, String variableName, Stage stage, double min, double max)  {
		this(new Node[] {node}, new String[] {variableName}, stage, min, max);
	}
	public FxTransformer(Node[] nodes, String[] names, Stage stage, double min, double max)  {
		Map<String, Node> map = new LinkedHashMap<>();

		for (int i = 0; i < nodes.length; i++) 
			map.put(names[i], nodes[i]);

		init(map, stage, min, max);
	}
	public FxTransformer(Map<String, Node> nodeMap, Stage stage, double min, double max) {
		init(nodeMap, stage, min, max);
	}
	private void init(Map<String, Node> nodeMap, Stage stage, double min, double max) {
		if(nodeMap.isEmpty())
			throw new IllegalArgumentException("nodeMap cannot be empty");

		initStyle(StageStyle.UTILITY);
		initOwner(stage);
		VBox root = new VBox(5);
		
		sliderValueChanger(root, stage);

		Consumer<Node> add = n -> root.getChildren().add(n);

		ComboBox<String> choices = new ComboBox<>(FXCollections.observableArrayList(nodeMap.keySet()));
		Text nameText = new Text();
		StringProperty nameProperty = new SimpleStringProperty();
		nameText.textProperty().bind(Bindings.concat(" variable: ", nameProperty));

		if(nodeMap.size() != 1)
			add.accept(choices);

		add.accept(nameText);
		add.accept(new Separator());

		Button copy = new Button("copy");
		Button copyAll = new Button("copy All");
		HBox hb = new HBox(5, copyAll, copy);
		hb.setPadding(new Insets(5));
		hb.setAlignment(Pos.CENTER_RIGHT);
		add.accept(hb);

		add.accept(new Separator());

		Accordion accordion = new Accordion();
		ScrollPane sp = new ScrollPane(accordion);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
		add.accept(sp);

		List<AbstractTransform> transforms = new ArrayList<>();

		Consumer<AbstractTransform> addTitlePane = at -> {
			accordion.getPanes().add(new TitledPane(at.title(), (Node)at));
			transforms.add(at);
		};

		addTitlePane.accept(new Translation(min, max));
		// addTitlePane.accept(new RotateUsingMethod());
		addTitlePane.accept(new RotateUsingTransform());
		addTitlePane.accept(new Scaling(min, max));
		addTitlePane.accept(new Shearing(min, max));

		copy.setOnAction(e -> {
			Optional.ofNullable(accordion.getExpandedPane())
			.map(TitledPane::getContent)
			.map(AbstractTransform.class::cast)
			.ifPresent(at -> {
				Map<DataFormat, Object> map = new HashMap<>();
				map.put(DataFormat.PLAIN_TEXT, at.toString());
				Clipboard.getSystemClipboard().setContent(map);	
			});
		});

		Set<Node> modifiedNodes = new LinkedHashSet<>();

		copyAll.setOnAction(e -> {
		});

		choices.getSelectionModel().selectedItemProperty()
		.addListener((p, o, n) -> {
			Node old = nodeMap.get(o),
					_new = nodeMap.get(n);

			if(old != null) 
				modifiedNodes.add(old);

			nameProperty.set(n);

			for (AbstractTransform t : transforms)
				t.change(old, _new);	
		});

		choices.getSelectionModel().select(0);
		setScene(new Scene(root));

		Platform.runLater(() -> {
			setX(stage.getX()+stage.getWidth());
			setY(stage.getY());
			setHeight(400);
		});
	}

	private static void sliderValueChanger(Parent root, Stage stage) {
		root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
			if(e.getClickCount() < 2 || !e.getTarget().getClass().getName().contains("SliderSkin"))
				return;
			
			Node node = (Node)e.getTarget();
			if(!node.getStyleClass().contains("thumb"))
				return;
			
			Slider sl = (Slider) node.getParent();
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(stage);
			
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(5);
			grid.setPadding(new Insets(10));
			
			TextField minTF = new TextField(String.valueOf(sl.getMin()));
			TextField maxTF = new TextField(String.valueOf(sl.getMax()));
			
			String msg = Optional.of(sl.getTooltip().getText())
					.map(s -> s.substring(0, s.lastIndexOf('[')))
					.get();
			
			alert.setHeaderText("set slider min, max\nfor: "+msg +"\nmin: "+sl.getMin()+"\nmax: "+sl.getMax());
			
			grid.addRow(0, new Text("min"), minTF );
			grid.addRow(1, new Text("max"), maxTF );
			
			alert.getDialogPane().setContent(grid);
			
			alert.showAndWait()
			.filter(s -> s == ButtonType.OK)
			.ifPresent(s -> {
				String status = "";
				
				try {
					double d = Double.parseDouble(minTF.getText().trim());
					if(d != sl.getMin()) {
						status += "min: "+ sl.getMin() + " -> "+d+"\n";
						sl.setMin(d);
					}
				} catch (NumberFormatException e2) {
					status += "min: "+ sl.getMin() + " -> failed: "+e2.getMessage()+"\n"; 
				}
				try {
					double d = Double.parseDouble(maxTF.getText().trim());
					if(d != sl.getMax()) {
						status += "max: "+ sl.getMax() + " -> "+d+"\n";
						sl.setMax(d);
					}
				} catch (NumberFormatException e2) {
					status += "max: "+ sl.getMax() + " -> failed: "+e2.getMessage()+"\n"; 
				}
				
				if(!status.isEmpty()) {
					sl.getTooltip().setText(msg + Arrays.toString(AbstractTransform.numbers2(sl.getMin(), sl.getMax())));
					Alert al = new Alert(AlertType.INFORMATION);
					al.initModality(Modality.WINDOW_MODAL);
					al.initOwner(stage);
					
					al.setHeaderText("Finished!");
					al.setContentText(status);
					al.show(); 
				}
			});
		});
	}
	public static Stage sliders(DoubleProperty property, String name, Stage stage2, double min, double max) {
		return sliders(new DoubleProperty[] {property}, new String[] {name},stage2, min, max);
	}
	public static Stage sliders(DoubleProperty[] properties, String[] names,Stage stage2, double min, double max) {
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.initOwner(stage2);
		
		Parent node = new ScrollPane(new Sliders(properties, names, min, max));
		stage.setScene(new Scene(node));
		sliderValueChanger(node, stage);

		Platform.runLater(() -> {
			stage.setX(stage.getX()+stage.getWidth());
			stage.setY(stage.getY());
			stage.sizeToScene();
		});
		return stage;
	}

}
