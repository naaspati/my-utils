package sam.fx.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import sam.fx.alert.FxAlert;
import sam.fx.helpers.FxButton;
import sam.fx.helpers.FxConstants;
import sam.fx.helpers.FxHBox;
import sam.fx.popup.FxPopupShop;
import sam.myutils.Checker;

public class TextAreaDialog {
	private final Stage stage = new Stage();
	private final Label header = new Label();
	private final TextArea textArea = new TextArea();
	private String result;
	private final BorderPane root;

	public TextAreaDialog() {
		this(null, null, null);
	}

	public TextAreaDialog(String title, String header, String content) {
		initModality(Modality.APPLICATION_MODAL);
		initStyle(StageStyle.UTILITY);

		Button importBtn = FxButton.button("import", e -> importAction());
		Button cancelBtn = FxButton.button("Cancel", e -> stage.hide());
		cancelBtn.setCancelButton(true);

		Button okBtn = FxButton.button("Ok", e -> okAction());
		Text ret = new Text();
		root = new BorderPane(textArea, this.header, null, FxHBox.buttonBox(ret, FxHBox.maxPane(), importBtn, cancelBtn, okBtn), null);
		stage.setScene(new Scene(root));
		
		textArea.textProperty().addListener(i -> Platform.runLater(() -> {
			String strs = textArea.getText();
			if(Checker.isEmpty(strs))
				ret.setText(null);
			else {
				long lines = strs.chars().filter(k -> k == '\n').count() + 1;
				ret.setText("lines: "+lines+", chars: "+strs.length());
			}
		}));

		setTitle(title);
		setHeaderText(header);
		setContent(content);

		root.getStyleClass().add("textarea-dialog");
		this.header.getStyleClass().add("header");
		BorderPane.setMargin(this.header, FxConstants.INSETS_10);
	}

	public void setContent(String text) {
		textArea.setText(text);
	}
	public void setHeader(Node header) {
		root.setTop(header);
	}
	public void setHeaderText(String header) {
		this.header.setText(header);
	}

	public Optional<String> showAndWait() {
		result = null;
		stage.showAndWait();
		return result == null ? Optional.empty() : Optional.of(result);
	}

	private void okAction() {
		result = textArea.getText();
		stage.hide();
	}

	private File importDir;
	private void importAction() {
		FileChooser fc = new FileChooser();
		if(importDir != null)
			fc.setInitialDirectory(importDir);
		ExtensionFilter e = new ExtensionFilter("text file", "*.txt", "*.text");
		fc.getExtensionFilters().addAll(e, new ExtensionFilter("all", "*.*"));
		fc.setSelectedExtensionFilter(e);

		fc.setTitle("choose file to import");

		File file = fc.showOpenDialog(stage);
		if(file == null) {
			FxPopupShop.showHidePopup("cancelled", 1500);
			return;
		}

		this.importDir = file.getParentFile();
		try {
			textArea.setText(new String(Files.readAllBytes(file.toPath()), "utf-8"));
		} catch (IOException e1) {
			FxAlert.showErrorDialog(file, "failed to load text", e1);
		}
	}
	public void setImportDir(File importDir) {
		this.importDir = importDir;
	}
	public final void initStyle(StageStyle style) {
		stage.initStyle(style);
	}
	public final void initModality(Modality modality) {
		stage.initModality(modality);
	}
	public final void initOwner(Window owner) {
		stage.initOwner(owner);
	}
	public void setTitle(String title) {
		stage.setTitle(title);
	}
}
