package meiko.application.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class deleteTagController {

	@FXML
	private TextField deleteTags;

	private Stage dialogStage;

	public static String deleteTags_str = "info attachment";
	private boolean isSet = false;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void initialize() {
		deleteTags.setText(deleteTags_str);
	}

	@FXML
	private void handleConfirm() {
		deleteTags_str = deleteTags.getText();
		isSet = true;
		dialogStage.close();
	}

	public boolean isSet() {
		// TODO Auto-generated method stub
		return isSet;
	}

}
