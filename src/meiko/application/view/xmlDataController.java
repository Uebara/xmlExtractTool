package meiko.application.view;

import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import meiko.application.model.Cases;
import meiko.application.model.ExtractCases;

public class xmlDataController {

	@FXML
	private TableView<Cases> caseTable;
	@FXML
	private TableColumn<Cases, String> idColumn;
	@FXML
	private TableColumn<Cases, String> caseColumn;
	@FXML
	private TableColumn<Cases, String> fileColumn;

	@FXML
	private TextArea contentText;

	private Stage dialogStage;
	private ObservableList<Cases> tableItems;
	private ArrayList<String> itemIdDelete = new ArrayList<String>();

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize table with id, caseName, xmlFileName.
		PropertyValueFactory<Cases, String> idProperty = new PropertyValueFactory<Cases, String>("id");
		PropertyValueFactory<Cases, String> caseProperty = new PropertyValueFactory<Cases, String>("caseName");
		PropertyValueFactory<Cases, String> fileProperty = new PropertyValueFactory<Cases, String>("xmlFileName");

		idColumn.setCellValueFactory(idProperty);
		caseColumn.setCellValueFactory(caseProperty);
		fileColumn.setCellValueFactory(fileProperty);

		// Show content detail by choosing table item.
		caseTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showContentDetails(newValue));

		// Set mouse right click to remove item.
		caseTable.setRowFactory(new Callback<TableView<Cases>, TableRow<Cases>>() {
			@Override
			public TableRow<Cases> call(TableView<Cases> tableView) {
				final TableRow<Cases> row = new TableRow<>();
				final ContextMenu contextMenu = new ContextMenu();
				final MenuItem removeMenuItem = new MenuItem("刪除");
				removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						itemIdDelete.add(row.getItem().getid());
						caseTable.getItems().remove(row.getItem());
					}
				});
				contextMenu.getItems().add(removeMenuItem);
				// Set context menu on row, but use a binding to make it only
				// show for non-empty rows:
				row.contextMenuProperty()
						.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
				return row;
			}
		});
	}

	private void showContentDetails(Cases cases) {
		// TODO Auto-generated method stub
		if (cases != null) {
			contentText.setText(cases.getCaseContent());
		} else {
			contentText.setText("");
		}
	}

	@SuppressWarnings("static-access")
	public void setCasesData(String filePath, String xpath) {
		// TODO Auto-generated method stub
		ExtractCases data = new ExtractCases(filePath, xpath);
		tableItems = data.getCasesData();
		caseTable.setItems(tableItems);
	}

	@FXML
	private void handleInsertDB() {
		try {
			int caseSum = ExtractCases.extratc(itemIdDelete);
			itemIdDelete.clear();

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("存入数据库成功！");
			alert.setContentText("一共存入" + caseSum + "个案例。");

			alert.showAndWait();
			if (!alert.isShowing()) {
				dialogStage.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
