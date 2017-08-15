package meiko.application.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BlendMode;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import meiko.application.xmlETMain;
import meiko.application.database.SQLiteDataBase;
import meiko.application.model.CaseDetails;
import meiko.application.model.Cases;
import meiko.application.model.TreeItemCreationContentHandler;

/**
 *
 * @author maulik.patel
 */
public class TreeLayoutController {
	/* =================== FXML Variable ================ */
	@FXML
	TreeView<String> view_root;
	@FXML
	Button show_tree;
	@FXML
	TextArea show_log;
	@FXML
	CheckBox isShowTag;
	@FXML
	CheckBox isHideTag;

	private boolean isShowAll = true;
	private xmlETMain main;

	public void setMainApp(xmlETMain main) {
		this.main = main;
	}

	private static TreeItem<String> root_item;
	private static TreeItem<String> root_item_cls;
	private static File file;

	public static String xpathWithIndex = "";
	private String recordItemValue = "";
	private int flagIndex = 1;
	private TreeItem<String> rangeItem;
	private ArrayList<TreeItem<String>> treeItemList = new ArrayList<>();

	@FXML
	private void initialize() {
		isShowTag.setDisable(true);
	}

	/* ================== Fxml Method =============== */
	@FXML
	private void handleOpen() throws SAXException, ParserConfigurationException, IOException {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

		// Show open file dialog
		file = fileChooser.showOpenDialog(main.getPrimaryStage());

		if (file != null) {
			isShowTag.setDisable(false);
			root_item = deleteCertainItem(TreeItemCreationContentHandler.readData(file));

			TreeItemCreationContentHandler.flag = false;
			root_item_cls = deleteCertainItem(TreeItemCreationContentHandler.readData(file));
			TreeItemCreationContentHandler.flag = true;

			view_root.setRoot(root_item);
			view_root.setEditable(true);
			view_root.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
				@Override
				public TreeCell<String> call(TreeView<String> p) {
					return new TextFieldTreeCellImpl();
				}
			});
			/* ============ Internal Method ================= */
			show_log.appendText("文件打开成功。\n");
		}
	}

	@FXML
	private void showAll() throws SAXException, ParserConfigurationException, IOException {
		if (!isShowAll) {
			isShowAll = true;
			view_root.setRoot(root_item);
		} else {
			isShowAll = false;
			view_root.setRoot(root_item_cls);
		}
	}

	@FXML
	private void handleDeleteTag() throws SAXException, ParserConfigurationException, IOException {
		if (main.showDeleteTagDialog() && file != null) {
			root_item = deleteCertainItem(TreeItemCreationContentHandler.readData(file));

			TreeItemCreationContentHandler.flag = false;
			root_item_cls = deleteCertainItem(TreeItemCreationContentHandler.readData(file));
			TreeItemCreationContentHandler.flag = true;

			if (!isShowAll) {
				view_root.setRoot(root_item_cls);
			} else {
				view_root.setRoot(root_item);
			}
		}
	}

	private boolean isDataBaseOpen = false;
	public static SQLiteDataBase db;

	@FXML
	private void handleDataBase() {
		if (isDataBaseOpen)
			return;
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("db files (*.db)", "*.db");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

		// Show open file dialog
		File file = fileChooser.showOpenDialog(main.getPrimaryStage());

		if (file == null)
			return;

		db = new SQLiteDataBase(file.getAbsolutePath());

		boolean bl = db.tableExist("cases");
		if (!bl) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("错误");
			alert.setHeaderText("数据库不存在");
			alert.setContentText("数据库中不存在cases表");

			alert.showAndWait();
			return;
		}

		dataBaseController.getCasesData().clear();
		isDataBaseOpen = true;
		List<Map<String, String>> resultList = db.queryList("select * from cases");
		for (int j = 0; j < resultList.size(); j++) {
			Map<String, String> map = (Map<String, String>) resultList.get(j);
			Cases oneCase = new Cases(map.get("ID"), map.get("CASE_NAME"), map.get("CONTENT"), map.get("XML_FILENAME"));
			dataBaseController.getCasesData().add(oneCase);
		}

		if (db.tableExist("caseDetail")) {
			dataBaseController.getCasesDetail().clear();
			List<Map<String, String>> detailList = db.queryList("select * from caseDetail");
			for (int j = 0; j < detailList.size(); j++) {
				Map<String, String> map = (Map<String, String>) detailList.get(j);
				CaseDetails oneCase = new CaseDetails(map.get("ID"), map.get("TITLE"), map.get("NAME"),
						map.get("CONTENT"));
//				if (!dataBaseController.geteditedContent().contains(map.get("TITLE")))
//					dataBaseController.geteditedContent().add(map.get("TITLE"));
				dataBaseController.getCasesDetail().add(oneCase);
			}
		}
		isDataBaseOpen = main.showDatabaseDialog();
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

	private final class TextFieldTreeCellImpl extends TreeCell<String> {

		private ContextMenu addMenu = new ContextMenu();
		private ContextMenu titleMenu = new ContextMenu();

		// R I G H T M O U S E C L I C K ! ! !
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public TextFieldTreeCellImpl() {
			MenuItem rangeMenuItem = new MenuItem("选中该范围");
			MenuItem xpathMenuItem = new MenuItem("选择案例");
			MenuItem singleMenuItem = new MenuItem("选择一个案例");

			addMenu.getItems().add(rangeMenuItem);
			addMenu.getItems().add(xpathMenuItem);
			addMenu.getItems().add(singleMenuItem);

			rangeMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {

					rangeItem = getTreeItem();
					treeItemList.add(rangeItem);

					flagIndex = 1;
					xpathWithIndex = "/*[name()='" + getTreeItem().getValue() + "'][" + loopPrevious(getTreeItem())
							+ "]";
					xpathWithIndex = loopTreeRange(getTreeItem(), xpathWithIndex);

					show_log.appendText("选中范围:" + rangeItem.getValue() + "\n");
					System.out.println(xpathWithIndex);
				}
			});

			xpathMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {

					TreeItem<String> Item = getTreeItem().getParent();
					while (!xpathWithIndex.equals("") && Item != rangeItem) {
						if (Item == null) {
							// Show the error message.
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("无效范围");
							alert.setHeaderText("案例不在选择范围内！");
							alert.setContentText("案例选择范围为：" + rangeItem.getValue());

							alert.showAndWait();
							return;
						} else
							Item = Item.getParent();
					}

					String xpath = "/*[name()='" + getTreeItem().getValue() + "']";
					xpath = loopTree(getTreeItem(), xpath);
					xpath = xpathWithIndex + xpath;

					xpathWithIndex = "";
					treeItemList.clear();
					System.out.println(xpath);
					show_log.appendText("抽取符合xpath:" + xpath + " 的所有案例。\n");

					/* add by like */
					String filePath = file.getAbsolutePath();
					try {
						main.showCasesEditDialog(filePath, xpath);
						// ExtractCases.extratc(filePath, xpath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			singleMenuItem.setOnAction(new EventHandler() {
				public void handle(Event t) {

					String singleCaesPath;
					rangeItem = getTreeItem();

					singleCaesPath = "/*[name()='" + getTreeItem().getValue() + "'][" + loopPrevious(getTreeItem())
							+ "]";
					singleCaesPath = loopTreeRange(getTreeItem(), singleCaesPath);

					show_log.appendText("选中案例:" + singleCaesPath + "\n");
					System.out.println(singleCaesPath);

					/* add by like */
					String filePath = file.getAbsolutePath();
					treeItemList.clear();
					try {
						main.showCasesEditDialog(filePath, singleCaesPath);
						// ExtractCases.extratc(filePath, xpath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			MenuItem titleMenueItem = new MenuItem("将该段设为标题");
			titleMenu.getItems().add(titleMenueItem);

			titleMenueItem.setOnAction(new EventHandler() {
				public void handle(Event t) {
					show_log.appendText(getTreeItem().getValue() + "将该段设为标题。\n");
				}
			});
		}

		private String loopTreeRange(TreeItem<String> treeItem, String rangeStr) {
			// TODO Auto-generated method stub
			while (treeItem.getParent() != null) {
				flagIndex = 1;
				treeItemList.add(treeItem.getParent());
				rangeStr = "/*[name()='" + treeItem.getParent().getValue() + "'][" + loopPrevious(treeItem.getParent())
						+ "]" + rangeStr;
				treeItem = treeItem.getParent();
			}
			return rangeStr;
		}

		private String loopTree(TreeItem<String> Item, String tempStr) {
			while (Item.getParent() != null && !treeItemList.contains(Item.getParent())) {
				tempStr = "/*[name()='" + Item.getParent().getValue() + "']" + tempStr;
				Item = Item.getParent();
			}
			return tempStr;
		}

		private int loopPrevious(TreeItem<String> item) {
			// TODO Auto-generated method stub
			TreeItem<String> tempItem = item.previousSibling();
			if (tempItem == null)
				return 1;
			recordItemValue = tempItem.getValue();

			while (recordItemValue == item.getValue()) {
				flagIndex++;
				loopPrevious(tempItem);
			}
			return flagIndex;
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				//// setGraphic(null);
			} else {
				setText(getString());
				// setText(null);
				//// setGraphic(getTreeItem().getGraphic());
				if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
					if (isHideTag.isSelected())
						setText(" ");
					setContextMenu(addMenu);
				} else if (getTreeItem().isLeaf()) {
					setContextMenu(null);
					// setText(getString());
				}
			}
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}