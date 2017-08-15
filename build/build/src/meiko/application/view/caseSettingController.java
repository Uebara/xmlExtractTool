package meiko.application.view;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import meiko.application.model.Cases;
import meiko.application.model.ExtractCases;

public class caseSettingController {

	@FXML
	private TextField keywords;
	@FXML
	private TextField Tags;
	@FXML
	private CheckBox isAdapt;

	private Stage dialogStage;
	private Cases cases;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setCurrentCase(Cases cases) {
		this.cases = cases;
	}

	@FXML
	private void handleConfirm() {
		getSettings(keywords.getText(), Tags.getText(), isAdapt.isSelected());
		dialogStage.close();
	}

	private void getSettings(String keywords, String tags, boolean isAdapt) {

		Map<String, String> tagKeyMap = new HashMap<>();
		String[] tagsArray = tags.split(";");
		String[] originKeyWords = keywords.split(";");

		if (tagsArray.length != originKeyWords.length) {
			System.out.println("关键字与标签数量不匹配");
			return;
		}

		for (int i = 0; i < originKeyWords.length; i++) {
			String[] splitkeywords = originKeyWords[i].split(" ");
			for (int j = 0; j < splitkeywords.length; j++) {
				tagKeyMap.put(splitkeywords[j].trim(), tagsArray[i].trim());
			}
		}

		ArrayList<String> caseStr = new ArrayList<String>();
		if (isAdapt) {
			List<Cases> caseDataList = dataBaseController.getCasesData();
			for (int i = 0; i < caseDataList.size(); i++) {
				if (caseDataList.get(i).getXmlFileName().equals(cases.getXmlFileName())) {
					caseStr.add(caseDataList.get(i).getCaseContent());
				}
			}
		} else {
			caseStr.add(cases.getCaseContent());
		}

		// String caseStr = cases.getCaseContent();
		try {
			ExtractCases.ExtractCaseDetail(caseStr, tagKeyMap);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
}
