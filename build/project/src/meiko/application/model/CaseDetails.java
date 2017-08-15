package meiko.application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CaseDetails {
	private final StringProperty detailId;
	private final StringProperty detailName;
	private final StringProperty detailFile;
	private final StringProperty detailContent;

	public CaseDetails(String detailId, String detailFile, String detailName, String detailContent) {
		this.detailId = new SimpleStringProperty(detailId);
		this.detailFile = new SimpleStringProperty(detailFile);
		this.detailName = new SimpleStringProperty(detailName);
		this.detailContent = new SimpleStringProperty(detailContent);
	}

	//////////// ---------detailId---------/////////////
	public String getdetailId() {
		return detailId.get();
	}

	public void setdetailId(String detailId) {
		this.detailId.set(detailId);
	}

	public StringProperty detailIdProperty() {
		return detailId;
	}

	//////////// ---------detailContent---------/////////////
	public String getdetailContent() {
		return detailContent.get();
	}

	public void setdetailContent(String detailContent) {
		this.detailContent.set(detailContent);
	}

	public StringProperty detailContentProperty() {
		return detailContent;
	}

	//////////// --------detailName----------/////////////
	public String getdetailName() {
		return detailName.get();
	}

	public void setdetailName(String detailName) {
		this.detailName.set(detailName);
	}

	public StringProperty detailNameProperty() {
		return detailName;
	}

	//////////// ---------detailFile---------/////////////
	public String getdetailFile() {
		return detailFile.get();
	}

	public void setdetailFile(String detailFile) {
		this.detailFile.set(detailFile);
	}

	public StringProperty detailFileProperty() {
		return detailFile;
	}
}
