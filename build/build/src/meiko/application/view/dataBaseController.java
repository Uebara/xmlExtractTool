package meiko.application.view;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import meiko.application.xmlETMain;
import meiko.application.model.CaseDetails;
import meiko.application.model.Cases;
import meiko.application.model.TreeItemCreationContentHandler;

public class dataBaseController {

	@FXML
	private TableView<Cases> caseTable;
	@FXML
	private TableColumn<Cases, String> idColumn;
	@FXML
	private TableColumn<Cases, String> caseColumn;
	@FXML
	private TableColumn<Cases, String> fileColumn;
//	@FXML
//	private TableColumn<Cases, String> classifyColumn;

	@FXML
	private TreeView<String> contentTree;
	private static TreeItem<String> contentItem;

	private final static ObservableList<Cases> casesData = FXCollections.observableArrayList();
	public static ObservableList<Cases> getCasesData() {
		return casesData;
	}

	//----------------------------------------------------

	@FXML
	private TableView<CaseDetails> detailTable;
	@FXML
	private TableColumn<CaseDetails, String> detailIdColumn;
	@FXML
	private TableColumn<CaseDetails, String> detailFileColumn;
	@FXML
	private TableColumn<CaseDetails, String> detailNameColumn;
//	@FXML
//	private TableColumn<Cases, String> classifyColumn;

	@FXML
	private TreeView<String> detailContentTree;
	private static TreeItem<String> detailContentItem;

	private final static ObservableList<CaseDetails> casesDetail = FXCollections.observableArrayList();
	public static ObservableList<CaseDetails> getCasesDetail() {
		return casesDetail;
	}

	//----------------------------------------------------

	private boolean okClicked = false;

//	private static ArrayList<String> editedContent = new ArrayList<>();
//
//	public static ArrayList<String> geteditedContent(){
//		return editedContent;
//	}

	private Stage dialogStage;

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		//---------------------------O R I G I N A L C A S E --- S E C T I O N---------------------------------------

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

		caseTable.setRowFactory(new Callback<TableView<Cases>, TableRow<Cases>>() {
			@Override
			public TableRow<Cases> call(TableView<Cases> tableView) {
				final ContextMenu contextMenu = new ContextMenu();

				// Set row color
				final TableRow<Cases> row = new TableRow<Cases>();// {
//					@Override
//					protected void updateItem(Cases cases, boolean empty) {
//						super.updateItem(cases, empty);
//						if (!empty && editedContent.contains(getItem().getCaseName())) {
//							setStyle("-fx-background-color:#95CACA");
//						} else if (!empty && !editedContent.contains(getItem().getid())) {
//							setStyle("");
//						}
//					}
//				};

				// Set context menu on row, but use a binding to make it only
				// show for non-empty rows:
				row.contextMenuProperty()
						.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));

				// Set right click---Set Extract
				final MenuItem extractMenuItem = new MenuItem("³éÈ¡ÉèÖÃ");
				extractMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						xmlETMain.showCaseSettingDialog(row.getItem());
					}
				});
				contextMenu.getItems().add(extractMenuItem);

				final MenuItem removeMenuItem = new MenuItem("„h³ý");
				removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						caseTable.getItems().remove(row.getItem());
						TreeLayoutController.db.delete("cases", "ID", row.getItem().getid());
					}
				});
				contextMenu.getItems().add(removeMenuItem);

				return row;
			}
		});

		//---------------------------C A S E D E T A I L --- S E C T I O N--------------------------------

		// Initialize table with id, caseName, xmlFileName.
		PropertyValueFactory<CaseDetails, String> detailIdProperty = new PropertyValueFactory<CaseDetails, String>("detailId");
		PropertyValueFactory<CaseDetails, String> detailNameProperty = new PropertyValueFactory<CaseDetails, String>("detailName");
		PropertyValueFactory<CaseDetails, String> detailFileProperty = new PropertyValueFactory<CaseDetails, String>("detailFile");

		detailIdColumn.setCellValueFactory(detailIdProperty);
		detailNameColumn.setCellValueFactory(detailNameProperty);
		detailFileColumn.setCellValueFactory(detailFileProperty);

		// Show content detail by choosing table item.
		detailTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showContentDetails(newValue));

		// Set mouse right click to remove item.
		detailTable.setRowFactory(new Callback<TableView<CaseDetails>, TableRow<CaseDetails>>() {
			@Override
			public TableRow<CaseDetails> call(TableView<CaseDetails> tableView) {
				final TableRow<CaseDetails> row = new TableRow<>();
				final ContextMenu contextMenu = new ContextMenu();
				final MenuItem removeMenuItem = new MenuItem("„h³ý");
				removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						detailTable.getItems().remove(row.getItem());
						TreeLayoutController.db.delete("caseDetail", "ID", row.getItem().getdetailId());
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

	/**
	 * Show XML content with TreeView
	 */
	//---------------------------C A S E D E T A I L --- S E C T I O N---------------------------------
	private void showContentDetails(CaseDetails caseDetail) {
		// TODO Auto-generated method stub
		if (caseDetail != null) {
			try {
				boolean temp = TreeItemCreationContentHandler.flag;
				TreeItemCreationContentHandler.flag = true;

				detailContentItem = deleteCertainItem(TreeItemCreationContentHandler.readDataStr(caseDetail.getdetailContent()));
				detailContentTree.setRoot(detailContentItem);

				TreeItemCreationContentHandler.flag = temp;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			detailContentTree.setRoot(null);
			// contentText.setText("");
		}
	}

	//---------------------------O R I G I N A L C A S E --- S E C T I O N-------------------------------------
	private void showContentDetails(Cases cases) {
		// TODO Auto-generated method stub
		if (cases != null) {
			try {
				boolean temp = TreeItemCreationContentHandler.flag;
				TreeItemCreationContentHandler.flag = true;

				contentItem = deleteCertainItem(TreeItemCreationContentHandler.readDataStr(cases.getCaseContent()));
				contentTree.setRoot(contentItem);

				TreeItemCreationContentHandler.flag = temp;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			contentTree.setRoot(null);
		}
	}

	/**
	 * Set Data to TableView. This method is called inxmlETMain showDatabaseDialog().
	 */
	public void setCasesData() {
		//---------------------------O R I G I N A L C A S E------------------------------
//		tableItems = ExtractCases.getCasesData();
		caseTable.setItems(casesData);

		//---------------------------C A S E D E T A I L----------------------------------
//		detailtableItems = ExtractCases.getCasesDetail();
		detailTable.setItems(casesDetail);
	}

	private TreeItem<String> deleteCertainItem(TreeItem<String> root) {
		for (int i = 0; i < root.getChildren().size(); i++) {
			if (deleteTagController.deleteTags_str.contains(root.getChildren().get(i).getValue())) {
				root.getChildren().remove(i--);
			} else if (!root.getChildren().get(i).getChildren().isEmpty()) {
				deleteCertainItem(root.getChildren().get(i));
			}
		}
		return root;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 *
	 * @return
	 */
	public boolean isOpen() {
		return okClicked;
	}

	@FXML
	private void handleClose() {
		dialogStage.close();
	}

}
