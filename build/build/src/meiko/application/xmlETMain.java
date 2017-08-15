package meiko.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import meiko.application.model.Cases;
import meiko.application.view.TreeLayoutController;
import meiko.application.view.caseSettingController;
import meiko.application.view.dataBaseController;
import meiko.application.view.deleteTagController;
import meiko.application.view.xmlDataController;

public class xmlETMain extends Application {

	private Stage primaryStage;
	private AnchorPane rootLayout;

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("XML图书信息抽取工具 绿色版免安装V1.0");

		initRootLayout();
	}

	private void initRootLayout() {
		// TODO Auto-generated method stub
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(xmlETMain.class.getResource("view/mainTreeLayout.fxml"));
			rootLayout = (AnchorPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(xmlETMain.class.getResourceAsStream("icon_blue_orange.png")));

			TreeLayoutController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showCasesEditDialog(String filePath, String xpath) {
		try {
			// Create the dialog Stage.
			Stage dialogStage = new Stage();

			dialogStage.setTitle("抽取结果展示");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			dialogStage.getIcons().add(new Image(xmlETMain.class.getResourceAsStream("icon_blue_orange.png")));

			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(xmlETMain.class.getResource("view/xmlDataOverview.fxml"));

			AnchorPane page = (AnchorPane) loader.load();
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			xmlDataController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCasesData(filePath, xpath);

			// Show dialog and wait until the user closes it
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Stage databaseDialog;

	public boolean showDatabaseDialog() {
		try {
			databaseDialog = new Stage();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(xmlETMain.class.getResource("view/dataBaseOverview.fxml"));

			TabPane page = (TabPane) loader.load();
			Scene scene = new Scene(page);
			databaseDialog.setScene(scene);

			databaseDialog.setTitle("数据库");
			databaseDialog.initModality(Modality.NONE);
			databaseDialog.initOwner(primaryStage);
			databaseDialog.getIcons().add(new Image(xmlETMain.class.getResourceAsStream("icon_blue_orange.png")));

			// Set the person into the controller.
			dataBaseController controller = loader.getController();
			controller.setDialogStage(databaseDialog);
			controller.setCasesData();

			databaseDialog.show();
			return controller.isOpen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void showCaseSettingDialog(Cases cases) {
		try {
			Stage caseSettingDialog = new Stage();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(xmlETMain.class.getResource("view/caseSettingOverview.fxml"));

			AnchorPane page = (AnchorPane) loader.load();
			Scene scene = new Scene(page);
			caseSettingDialog.setScene(scene);

			caseSettingDialog.setTitle("抽取设置");
			caseSettingDialog.initOwner(databaseDialog);
			caseSettingDialog.getIcons().add(new Image(xmlETMain.class.getResourceAsStream("icon_blue_orange.png")));
			caseSettingDialog.setResizable(false);

			caseSettingController controller = loader.getController();
			controller.setDialogStage(caseSettingDialog);
			controller.setCurrentCase(cases);

			caseSettingDialog.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean showDeleteTagDialog() {
		try {
			Stage deleteTagDialog = new Stage();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(xmlETMain.class.getResource("view/deleteTagSetting.fxml"));

			AnchorPane page = (AnchorPane) loader.load();
			Scene scene = new Scene(page);
			deleteTagDialog.setScene(scene);

			deleteTagDialog.setTitle("设置不显示的标签");
			deleteTagDialog.initOwner(primaryStage);
			deleteTagDialog.getIcons().add(new Image(xmlETMain.class.getResourceAsStream("icon_blue_orange.png")));
			deleteTagDialog.setResizable(false);

			deleteTagController controller = loader.getController();
			controller.setDialogStage(deleteTagDialog);

			deleteTagDialog.showAndWait();
			return controller.isSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
